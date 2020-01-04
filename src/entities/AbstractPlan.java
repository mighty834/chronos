package entities;
import java.util.*;
import java.text.*;
import exceptions.*;
import bridge.*;

abstract class AbstractPlan {
    private static final String dateFormat = "yyyy.MM.dd";
    private Date date;
    private ArrayList<Task> tasks;
    private String summary;
    private AbstractPlan retry;
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
        this.pushToStorage();
    }

    AbstractPlan(int ordinal, Date date) {
        this.date = date;
        this.ordinal = ordinal;
        this.pushToStorage();
    }

    AbstractPlan(int ordinal, String date) {
        SimpleDateFormat format = new SimpleDateFormat(dateFormat);

        try {
            this.date = format.parse(date);
            this.ordinal = ordinal;
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

    public void setState(String config) throws EntitySetStateException {
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        boolean callerCheck = false;

        for (StackTraceElement element: elements) {
            if (element instanceof IAbstractReader) callerCheck = true;
        }

        if (callerCheck) {
            
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
        this.retry = new this.getClass();
        this.close(summary);
        
        for (this.Task task: this.tasks) {
            if (!task.isDone()) {
                this.retry.tasks.add(task);
            }
        }
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

