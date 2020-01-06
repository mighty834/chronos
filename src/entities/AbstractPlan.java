package entities;
import java.util.*;
import java.text.*;
import exceptions.*;
import bridge.*;

abstract class AbstractPlan {
    private static final String DATE_FORMAT = "dd.MM.yyyy";
    private Date date;
    private ArrayList<Task> tasks;
    private String summary;
    private boolean retry = false;
    private boolean status;
    private int result;
    private double estimationDiff;
    private int ordinal;

    protected void pushToStorage() throws OrdinalAlreadyExistException {
        if (Storage.getAllPlans(this.getPlanType()).get(this.ordinal - 1) != null) {
            throw new OrdinalAlreadyExistException(this.getPlanType(), this.ordinal);
        } else {
            Storage.addPlan(this);
        }
    }

    AbstractPlan(int ordinal) {
        this.date = new Date();
        this.ordinal = ordinal;
        this.status = false;
        this.pushToStorage();
    }

    AbstractPlan(int ordinal, Date date) {
        this.date = date;
        this.ordinal = ordinal;
        this.status = false;
        this.pushToStorage();
    }

    AbstractPlan(int ordinal, String date) {
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);

        try {
            this.date = format.parse(date);
            this.ordinal = ordinal;
            this.status = false;
            this.pushToStorage();
        }
        catch (ParseException exception) {
            System.out.println(
                "Problem with date parsing in Plan entity!\n" +
                exception.getMessage()
            );
        }
    }

    abstract public String getPlanType();

    public void setState(ArrayList<String> params) throws EntitySetStateException {
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        boolean callerCheck = false;

        for (StackTraceElement element: elements) {
            if (element instanceof IAbstractReader) callerCheck = true;
        }

        if (callerCheck) {
            SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
            try {
                this.date = format.parse(params.get(0));
                if (params.get(1).indexOf("retry") != -1) this.retry = true;
                if (params.get(1).idnexOf("closed") != -1) this.status = true;

                for (int i = 2; i < params.size() - 1; i++) {
                    String temp = params.get(i);
                    boolean status = (temp.indexOf("[x]") != -1);
                    String theses = temp.substring(
                        temp.indexOf("]") + 1, temp.indexOf("{") - 1
                    );
                    double estimate = Double.parseDouble(
                        temp.substring(
                            temp.indexOf("}") + 2, temp.indexOf("h")        
                        )
                    );
                    String types = temp.substring(
                        temp.indexOf("{") + 1, temp.indexOf("}")        
                    );
                    String description = (temp.indexOf("|") != -1) ?
                    temp.substring(temp.indexOf("|") + 2) : null;

                    this.Task task = new this.Task(theses, estimate, types);

                    if (description != null) task.setDescription(description);
                    if (temp.indexOf("h") != temp.lastIndexOf("h")) {
                        double realEstimate = Double.parseDouble(
                            temp.substring(
                                temp.indexOf("h") + 2, temp.lastIndexOf("h")    
                            )
                        );

                        task.setRealVolume(realEstimate);
                    }

                    this.addTask(task);
                }

                this.summary = params.get(params.size() - 1);
            }
            catch (ParseException exception) {
                System.out.println(
                    "Problem with date parsing in setState method of entity!\n" +
                    exception        
                );
            }

        } else {
            throw new EntitySetStateException(this.getPlanType());
        }
    }

    public void addTask(Task task) {
        this.tasks.add(task);
    }

    public void addTask(String theses, double estimate, String types) {
        this.tasks.add(new Task(theses, estimate, types));
    }

    public void addTask(String theses, double estimate, String types, String description) {
        this.tasks.add(new Task(theses, estimate, types, description));
    }

    public void close() {
        double allEstimationTime = 0;
        double allClosedTime = 0;

        for (Task task: this.tasks) {
            allEstimationTime += task.getEstimateVolume();

            if (task.isDone()) {
                allClosedTime += task.getEstimateVolume();
                this.estimationDiff += task.getEstimationDiff();
            } else {
                this.estimationDiff -= task.getEstimateVolume();
            }
        }

        this.result = Math.round(allClosedTime / (allEstimationTime / 100));
    }

    public void close(String summary) {
        this.close();
        this.summary = summary;
    }

    public void close(String summary, boolean isRetry) {
        AbstractPlan plan = new this.getClass()(this.ordinal + 1, 1);
        plan.setRetry(isRetry);
        
        this.close(summary);
        
        for (this.Task task: this.tasks) {
            if (!task.isDone()) {
                plan.addTask(new this.Task(task));
            }
        }
    }

    public boolean setRetry(boolean isRetry) {
        this.retry = isRetry;
    }

    public boolean isRetry() {
        return this.retry;
    }

    public boolean isClosed() {
        return this.status;
    }

    public int getOrdinal() {
        return this.ordinal;
    }

    public Task getTask(int index) throws TaskNotExistException {
        if ((index >= this.tasks.size()) || (index < 0)) throw new TaskNotExistException(index);

        return this.tasks.get(index);
    }

    public Task getTask(String thesesPart) throws TaskNotExistException {
        Task result = null;
        
        for (Task task: tasks) {
            if (task.getTheses().indexOf(thesesPart) != -1) {
                result = task;
            }
        }

        if (result == null) throw new TaskNotExistException(thesesPart);
        return result;
    }

    enum ActivityTypes = { WORK, FUN, ROUTINE, GROWTH };

    class Task {
        private boolean status;
        private String theses;
        private String description;
        private double estimateVolume;
        private double realVolume;
        private ArrayList<ActivityTypes> types;

        //TODO Maybe possible use another way for types
        Task(Task task) {
            String types = "";
            
            if (task.isWork()) types = "work";
            if (task.isFun()) {
                if (types.length() > 0) types += ",fun";
                else types = "fun";
            }
            if (task.isRoutine()) {
                if (types.length() > 0) types += ",routine";
                else types = "routine";
            }
            if (task.isGrowth()) {
                if (types.length() > 0) types += ",growth";
                else types = "growth";
            }

            Task tempTask = new Task(
                task.getTheses(),
                task.getEstimateVolume(),
                types
            );

            if (task.getDescription() != null) {
                tempTask.setDescription(task.getDescription());
            }

            return tempTask;
        }

        Task(String theses, double estimate, String types) {
            this.status = false;
            this.theses = theses;
            this.estimateVolume = estimate;
            this.types = new ArrayList<ActivityTypes>();

            for (String value: types.split(",")) {
                this.types.add(value.toUpperCase());
            }
        }

        Task(String theses, double estimate, String types, String description) {
            Task(theses, estimate, types);
            this.description = description;
        }

        public void close(double realVolume) {
            this.realVolume = realVolume;
            this.status = true;
        }

        public void setStatus(boolean status) {
            this.status = status;
        }

        public void setRealVolume(double realVolume) {
            this.realVolume = realVolume;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public boolean isDone() {
            return this.status;
        }

        public String getTheses() {
            return this.theses;
        }

        public String getDescription() {
            return (this.description != null) ? this.description :
            "Sorry, but this task haven't description";
        }

        public double getEstimateVolume() {
            return this.estimateVolume;
        }

        public double getRealVolume() {
            return this.realVolume;
        }

        public double getEstimationDiff() throws OpenTaskEstimationDiffException {
            if (this.isDone()) return this.estimateVolume - this.realVolume;
            else throw new OpenTaskEstimationDiffException();
        }

        public boolean isWork() {
            return this.types.indexOf(WORK) != -1;
        }

        public boolean isFun() {
            return this.types.indexOf(FUN) != -1;
        }

        public boolean isRoutine() {
            return this.types.indexOf(ROUTINE) != -1;
        }

        public boolean isGrowth() {
            return this.types.indexOf(GROWTH) != -1;
        }
    }
}

