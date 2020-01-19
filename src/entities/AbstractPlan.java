package entities;
import java.util.*;
import java.text.*;
import exceptions.*;
import bridge.*;

public abstract class AbstractPlan {
    public static final String DATE_FORMAT = "dd.MM.yyyy";
    protected Date date;
    protected boolean status;
    protected int ordinal;
    protected ArrayList<Task> tasks;
    private String summary = null;
    private boolean retry = false;
    private long result;
    private double estimationDiff;

    protected void pushToStorage()
    throws OrdinalAlreadyExistException, StorageUnexistingTypeException {
        if (Storage.getAllPlans(this.getPlanType()).size() > 0) {
            if (Storage.getAllPlans(this.getPlanType()).get(this.ordinal - 1) != null) {
                throw new OrdinalAlreadyExistException(this.getPlanType(), this.ordinal);
            } else {
                Storage.addPlan(this);
            }
        } else {
            Storage.addPlan(this);
        }
    }

    //TODO this mock must be remove
    public AbstractPlan() {
        System.out.println("Fuck, plan constructor can't bet empty!");
    }

    public AbstractPlan(int ordinal)
    throws OrdinalAlreadyExistException, StorageUnexistingTypeException {
        this.date = new Date();
        this.ordinal = ordinal;
        this.status = false;
        this.tasks = new ArrayList<>();
        this.pushToStorage();
    }

    public AbstractPlan(int ordinal, Date date)
    throws OrdinalAlreadyExistException, StorageUnexistingTypeException {
        this.date = date;
        this.ordinal = ordinal;
        this.status = false;
        this.tasks = new ArrayList<>();
        this.pushToStorage();
    }

    public AbstractPlan(int ordinal, String date)
    throws OrdinalAlreadyExistException, StorageUnexistingTypeException {
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);

        try {
            this.date = format.parse(date);
            this.ordinal = ordinal;
            this.status = false;
            this.tasks = new ArrayList<>();
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
            if (element.getMethodName().equals("loadEntities")) callerCheck = true;
        }

        if (callerCheck) {
            SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
            try {
                this.date = format.parse(params.get(0));
                if (params.get(1).indexOf("retry") != -1) this.retry = true;
                if (params.get(1).indexOf("closed") != -1) this.status = true;

                for (int i = 2; i < params.size(); i++) {
                    String temp = params.get(i);
                    boolean status = (temp.indexOf("[x]") != -1);
                    String theses = temp.substring(
                        temp.indexOf("]") + 2, temp.indexOf("{") - 1
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

                    Task task = new Task(theses, estimate, types);
                    
                    task.setStatus(status);
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

                if (params.get(params.size() - 1).indexOf("summary | ") != -1) {
                    String line = params.get(params.size() - 1);
                    this.summary = line.substring(10);
                }
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

    public void close() throws OpenTaskEstimationDiffException {
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

    public void close(String summary) throws OpenTaskEstimationDiffException {
        this.close();
        this.summary = summary;
    }

    public void close(String summary, boolean isRetry)
    throws OpenTaskEstimationDiffException, InstantiationException,
    IllegalAccessException {
        //TODO rewrite deprecation method
        @SuppressWarnings("deprecation")
        AbstractPlan plan = this.getClass().newInstance();
        plan.setOrdinal(this.ordinal + 1);
        plan.setDate(new Date(this.date.getTime() + 86400000));
        plan.setRetry(isRetry);
        
        this.close(summary);
        
        for (Task task: this.tasks) {
            if (!task.isDone()) {
                plan.addTask(new Task(task));
            }
        }
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setOrdinal(int ordinal) {
       this.ordinal = ordinal;
    } 

    public void setRetry(boolean isRetry) {
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

    public Date getDate() {
        return this.date;
    }

    public ArrayList<Task> getTasks() {
        return this.tasks;
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

    public String getSummary() {
        return this.summary;
    }

    public enum ActivityTypes { WORK, FUN, ROUTINE, GROWTH}

    public class Task {
        private boolean status;
        private String theses;
        private String description;
        private double estimateVolume = 0;
        private double realVolume;
        private ArrayList<ActivityTypes> types;

        public Task(Task task) {
            this(
                task.getTheses(),
                task.getEstimateVolume(),
                task.getTypes() 
            );

            if (task.getDescription() != null) {
                this.description = task.getDescription();
            }
        }

        public Task(String theses, double estimate, String types) {
            this.status = false;
            this.theses = theses;
            this.estimateVolume = estimate;
            this.description = null;
            this.types = new ArrayList<ActivityTypes>();
            types = types.toUpperCase();

            for (String value: types.split(",")) {
                switch (value) {
                    case "WORK": this.types.add(ActivityTypes.WORK);
                    break;
                    case "FUN": this.types.add(ActivityTypes.FUN);
                    break;
                    case "ROUTINE": this.types.add(ActivityTypes.ROUTINE);
                    break;
                    case "GROWTH": this.types.add(ActivityTypes.GROWTH);
                    break;
                    default: System.out.println("Pass wrong activity type in Task constructor");
                }
            }
        }

        public Task(String theses, double estimate, String types, String description) {
            this(theses, estimate, types);
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
            return this.description;
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

        public String getTypes() {
            String result = "";

            for (ActivityTypes type: this.types) {
                if (result.length() == 0) result = type.toString();
                else result += "," + type.toString();
            }

            return result;
        }

        public boolean isWork() {
            return this.types.indexOf(ActivityTypes.WORK) != -1;
        }

        public boolean isFun() {
            return this.types.indexOf(ActivityTypes.FUN) != -1;
        }

        public boolean isRoutine() {
            return this.types.indexOf(ActivityTypes.ROUTINE) != -1;
        }

        public boolean isGrowth() {
            return this.types.indexOf(ActivityTypes.GROWTH) != -1;
        }
    }
}

