package entities;;
import java.util.*;
import java.text.*;
import java.time.*;
import exceptions.*;

abstract class AbstractAim {
    private ArrayList<DodPoint> dod;
    private Date deadLine;
    private Date date;
    private Date startDate;
    private Date freezeDate;
    private int ordinal;
    private String description;
    private ArrayList<HistoryPoint> history;
    private AimActions status;
    private String dateFormat;
    private Postmortem postmortem;
    private String summary;
    private static final HashMap<AimActions, ArrayList<AimActions>> allowFlows;

    AbstractAim() {
        this.dod = new ArrayList<DodPoint>();
        this.history = new ArrayList<HistoryPoint>();
        this.date = new Date();
        this.deadLine = null;
        this.status = AimActions.DRAFT;
        this.dateFormat = "yyyy.MM.dd";
        this.ordinal = 0;
        this.postmortem = null;
        this.summary = null;

        // TODO maybe it can be write in some more pretty way...
        if (allowFlows == null) {
            ArrayList<AimActions> tempList = new ArrayList<>();
            this.allowFlows = new HashMap<>();
            
            // Allowed for START status
            tempList = new ArrayList<>();
            tempList.add(AimActions.DRAFT);
            tempList.add(AimActions.FREEZE);
            tempList.add(AimActions.MODIFY);
            this.allowFlows.put(AimActions.START, tempList);

            // Allowed for CLOSE status
            tempList = new ArrayList<>();
            tempList.add(AimActions.START);
            tempList.add(AimActions.FREEZE);
            tempList.add(AimActions.UNFREEZE);
            tempList.add(AimActions.MODIFY);
            this.allowFlows.put(AimActions.CLOSE, tempList);

            // Allowed for FREEZE status
            tempList = new ArrayList<>();
            tempList.add(AimActions.START);
            tempList.add(AimActions.UNFREEZE);
            tempList.add(AimActions.MODIFY);
            this.allowFlows.put(AimActions.FREEZE, tempList);

            // Allowed for REJECT status
            tempList = new ArrayList<>();
            tempList.add(AimActions.DRAFT);
            tempList.add(AimActions.START);
            tempList.add(AimActions.FREEZE);
            tempList.add(AimActions.UNFREEZE);
            tempList.add(AimActions.MODIFY);
            this.allowFlows.put(AimActions.REJECT, tempList);
        }
    }

    enum AimActions = { DRAFT, START, CLOSE, FREEZE, UNFREEZE, MODIFY, REJECT };

    private void commonAction(AimActions action) {
        this.status = action;
        this.history.add(new HistoryPoint(action));
    }

    private void beforeEachAction(AimActions action) throws AimNotAllowedActionFlow {
        if (!allowFlows.get(action).contains(this.status)) {
            ArrayList<String> possibleStatuses = new ArrayList<>();
            for (AimActions status: allowFlows.get(action)) {
                possibleStatuses.add(status);
            }

            throw new AimNotAllowedActionFlow(this.status.getValue(), possibleStatuses);
        }
    }

    public void start() throws AimStartLostPropertiesException, AimNotAllowedActionFlow {
       this.beforeEachAction(AimActions.START);

       if (this.status.equals(AimActions.DRAFT)) {
           ArrayList<String> missingProperties = new ArrayList<String>();
           if (this.dod.size() < 1) missingProperties.add("dod");
           if (this.deadLine == null) missingProperties.add("deadLine");
           if (this.ordinal == 0) missingProperties.add("ordinal");
           
           if (missingProperties.size() == 0) {
               this.startDate = new Date();
               this.commonAction(AimActions.START);
           } else {
               throw new AimStartLostPropertiesException(missingProperties);
           }
       } else {
           this.deadLine = new Date(
               this.deadLine.getTime() + (new Date().getTime() - this.freezeDate.getTime())    
           );
           this.commonAction(AimActions.UNFREEZE);
       }
    }

    public void close() throws AimCloseUndoneWithoutPostmortemException,
    AimCloseOverdueWithoutPostmortemException {
        this.beforeEachAction(AimActions.CLOSE);

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

        this.commonAction(AimActions.CLOSE);
    }

    public void freeze() {
        this.beforeEachAction(AimActions.FREEZE);
        
        this.freezeDate = new Date();
        this.commonAction(AimActions.FREEZE);
    }
        

    public void setPostmortem(String conclusion, String cause, String ... moreCauses) {
        this.postmortem = new Postmortem(conclusion, cause, moreCauses);
        
        if (!this.status.equals(AimActions.DRAFT)) this.commonAction(AimActions.MODIFY);
    }
    
    public void setSummary(String summary) {
        this.summary = summary;

        if (!this.status.equals(AimActions.DRAFT)) this.commonAction(AimActions.MODIFY);
    }

    public void setDodPoint(String theses) {
        this.dod.add(new DodPoint(theses));

        if (!this.status.equals(AimActions.DRAFT)) this.commonAction(AimActions.MODIFY);
    }

    public void setOrdinal(int ordinal) {
        this.ordinal = ordinal;

        if (!this.status.equals(AimActions.DRAFT)) this.commonAction(AimActions.MODIFY);
    }

    public void setDeadLine(Date deadLine) {
        this.deadLine = deadLine;
        
        if (!this.status.equals(AimActions.DRAFT)) this.commonAction(AimActions.MODIFY);
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

        if (!this.status.equals(AimActions.DRAFT)) this.commonAction(AimActions.MODIFY);
    }

    public void setDescription(String description) {
        this.description = description;

        if (!this.status.equals(AimActions.DRAFT)) this.commonAction(AimActions.MODIFY);
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

    public AimActions getStatus() {
        return this.status;
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
        private AimActions action;
        private Date date;

        HistoryPoint(AimAction action) {
            this.action = action;
            this.date = new Date();
        }

        public AimAction getAction() {
            return this.action;
        }

        public AimAction getStrAction() {
            return this.action.getValue();
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

