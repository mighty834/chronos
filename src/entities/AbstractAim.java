package entities;;
import java.util.*;
import java.text.*;
import java.time.*;
import exceptions.*;
import bridge.Storage;

abstract class AbstractAim {
    private ArrayList<DodPoint> dod;
    private Date deadLine;
    private Date date;
    private Date startDate;
    private Date freezeDate;
    private int ordinal;
    private String description;
    private ArrayList<HistoryPoint> history;
    private AimStatuses status;
    private String dateFormat;
    private Postmortem postmortem;
    private String summary;
    private static final HashMap<AimStatuses, ArrayList<AimStatuses>> allowFlows;

    protected void pushToStorage() throws OrdinalAlreadyExistException {
        if (Storage.getAllAims(this.getAimType()).get(this.ordinal - 1) != null) {
            throw new OrdinalAlreadyExistException(this.getAimType(), this.ordinal);
        } else {
            Storage.addAim(this);
        }
    }

    AbstractAim(int ordinal) {
        this.dod = new ArrayList<DodPoint>();
        this.history = new ArrayList<HistoryPoint>();
        this.date = new Date();
        this.deadLine = null;
        this.status = AimStatuses.DRAFT;
        this.dateFormat = "yyyy.MM.dd";
        this.ordinal = ordinal;
        this.postmortem = null;
        this.summary = null;

        // TODO maybe it can be write in some more pretty way...
        if (allowFlows == null) {
            ArrayList<AimStatuses> tempList = new ArrayList<>();
            this.allowFlows = new HashMap<>();
            
            // Allowed statuses for start() action
            tempList = new ArrayList<>();
            tempList.add(AimStatuses.DRAFT);
            tempList.add(AimStatuses.FREEZE);
            tempList.add(AimStatuses.MODIFY);
            this.allowFlows.put(AimStatuses.START, tempList);

            // Allowed statuses for close() action
            tempList = new ArrayList<>();
            tempList.add(AimStatuses.START);
            tempList.add(AimStatuses.FREEZE);
            tempList.add(AimStatuses.UNFREEZE);
            tempList.add(AimStatuses.MODIFY);
            this.allowFlows.put(AimStatuses.CLOSE, tempList);

            // Allowed statuses for freeze() action
            tempList = new ArrayList<>();
            tempList.add(AimStatuses.START);
            tempList.add(AimStatuses.UNFREEZE);
            tempList.add(AimStatuses.MODIFY);
            this.allowFlows.put(AimStatuses.FREEZE, tempList);

            // Allowed statuses for reject() action
            tempList = new ArrayList<>();
            tempList.add(AimStatuses.DRAFT);
            tempList.add(AimStatuses.START);
            tempList.add(AimStatuses.FREEZE);
            tempList.add(AimStatuses.UNFREEZE);
            tempList.add(AimStatuses.MODIFY);
            this.allowFlows.put(AimStatuses.REJECT, tempList);
        }

        this.pushToStorage();
    }

    enum AimStatuses = { DRAFT, START, CLOSE, FREEZE, UNFREEZE, MODIFY, REJECT };

    private void commonAction(AimStatuses status) {
        this.status = status;
        this.history.add(new HistoryPoint(status));
    }

    private void beforeEachAction(AimStatuses status) throws AimNotAllowedActionFlow {
        if (!allowFlows.get(status).contains(this.status)) {
            ArrayList<String> possibleStatuses = new ArrayList<>();
            for (AimStatuses oneStatus: allowFlows.get(status)) {
                possibleStatuses.add(oneStatus.getValue());
            }

            throw new AimNotAllowedActionFlow(this.status.getValue(), possibleStatuses);
        }
    }

    abstract public String getAimType();

    public void start() throws AimStartLostPropertiesException, AimNotAllowedActionFlow {
       this.beforeEachAction(AimStatuses.START);

       if (this.status.equals(AimStatuses.DRAFT)) {
           ArrayList<String> missingProperties = new ArrayList<String>();
           if (this.dod.size() < 1) missingProperties.add("dod");
           if (this.deadLine == null) missingProperties.add("deadLine");
           
           if (missingProperties.size() == 0) {
               this.startDate = new Date();
               this.commonAction(AimStatuses.START);
           } else {
               throw new AimStartLostPropertiesException(missingProperties);
           }
       } else {
           this.deadLine = new Date(
               this.deadLine.getTime() + (new Date().getTime() - this.freezeDate.getTime())    
           );
           this.commonAction(AimStatuses.UNFREEZE);
       }
    }

    public void close() throws AimCloseUndoneWithoutPostmortemException,
    AimCloseOverdueWithoutPostmortemException {
        this.beforeEachAction(AimStatuses.CLOSE);

        ArrayList<String> undone = new ArrayList<String>();
        Date currentDate = new Date();
        for (DodPoint dod: this.dod) {
            if (!dod.status) undone.add(dod.getTheses());
        }

        if ((undone.size() > 0) && (this.postmortem == null)) {
            throw new AimCloseUndoneWithoutPostmortemException(undone);
        }

        if ((currentDate.compareTo(this.deadLine) == 1) && (this.postmortem == null)) {
            throw new AimCloseOverdueWithoutPostmortemException();
        }

        this.commonAction(AimStatuses.CLOSE);
    }

    public void freeze() {
        this.beforeEachAction(AimStatuses.FREEZE);
        
        this.freezeDate = new Date();
        this.commonAction(AimStatuses.FREEZE);
    }
    
    public void reject() throws AimRejectNotHavePostmortem {
        this.beforeEachAction(AimStatuses.REJECT);

        if (!this.status.equals(AimStatuses.DRAFT)) {
            if (this.postmortem == null) {
                throw new AimRejectNotHavePostmortem();
            }
        }

        this.commonAction(AimStatuses.REJECT);
    }    

    public void setPostmortem(String conclusion, String cause, String ... moreCauses) {
        this.postmortem = new Postmortem(conclusion, cause, moreCauses);
        
        if (!this.status.equals(AimStatuses.DRAFT)) this.commonAction(AimStatuses.MODIFY);
    }
    
    public void setSummary(String summary) {
        this.summary = summary;

        if (!this.status.equals(AimStatuses.DRAFT)) this.commonAction(AimStatuses.MODIFY);
    }

    public void setDodPoint(String theses) {
        this.dod.add(new DodPoint(theses));

        if (!this.status.equals(AimStatuses.DRAFT)) this.commonAction(AimStatuses.MODIFY);
    }

    public void setOrdinal(int ordinal) {
        this.ordinal = ordinal;

        if (!this.status.equals(AimStatuses.DRAFT)) this.commonAction(AimStatuses.MODIFY);
    }

    public void setDeadLine(Date deadLine) {
        this.deadLine = deadLine;
        
        if (!this.status.equals(AimStatuses.DRAFT)) this.commonAction(AimStatuses.MODIFY);
    }

    public void setDeadLine(String deadLine) {
        SimpleDateFormat format = new SimpleDateFormat(dateFormat);
        
        try {
            this.setDeadLine(format.parse(deadLine));
        }
        catch (ParseException exception) {
            System.out.pritnln("Problem with date parsing!\n" + exception.getMessage());
        }
    }

    public void setDateFormat(String format) {
        this.dateFormat = format;

        if (!this.status.equals(AimStatuses.DRAFT)) this.commonAction(AimStatuses.MODIFY);
    }

    public void setDescription(String description) {
        this.description = description;

        if (!this.status.equals(AimStatuses.DRAFT)) this.commonAction(AimStatuses.MODIFY);
    }

    public Date getStartedDate() {
        return this.date;
    }

    public ArrayList<DodPoint> getDod() {
        return this.dod;
    }

    public Date getDeadLine() {
        return this.deadLine;
    }

    public String getDescription() {
        return this.description;
    }

    public ArrayList<HistoryPoint> getHistory() {
        return this.history;
    }

    public AimStatuses getStatus() {
        return this.status;
    }

    public int getOrdinal() {
        return this.ordinal;
    }

    class Postmortem {
        private ArrayList<String> causes;
        private String conclusion;
        private Date date;

        Postmortem(String conclusion, String cause, String ... moreCauses) {
            this.date = new Date();
            this.causes = new ArrayList<String>();
            this.conclusion = conclusion;
            this.causes.add(cause);
        
            for (String cause: moreCauses) {
                this.causes.add(cause);
            }
        }

        public Date getDate() {
            return this.date;
        }

        public String getConclusion() {
            return this.conclusion;
        }

        public ArrayList<String> getCauses() {
            return this.causes;
        }
    }

    class HistoryPoint {
        private AimStatuses status;
        private Date date;

        HistoryPoint(AimStatuses status) {
            this.status = status;
            this.date = new Date();
        }

        public AimStatuses getStatus() {
            return this.status;
        }

        public AimStatuses getStrStatus() {
            return this.status.getValue();
        }

        public Date getDate() {
            return this.date;
        }
    }

    class DodPoint {
        private boolean status = false;
        private String theses;

        DodPoint(String theses) {
            this.theses = theses;
        }

        public boolean isDone() {
            return this.status;
        }

        public String getTheses() {
            return this.theses;
        }

        public void setTheses(String theses) {
            this.theses = theses;
        }

        public void close() {
            this.status = true;
        }
    }
}

