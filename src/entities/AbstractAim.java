package entities;;
import java.util.*;
import java.text.*;
import java.time.*;
import exceptions.*;
import bridge.*;

public abstract class AbstractAim {
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
    private static HashMap<AimStatuses, ArrayList<AimStatuses>> allowFlows;
    protected ArrayList<Integer> affectedEntitiesOrdinals;
    
    protected void pushToStorage()
    throws OrdinalAlreadyExistException, StorageUnexistingTypeException {
        if (Storage.getAllAims(this.getAimType()).size() > this.ordinal - 1) {
            if (Storage.getAllAims(this.getAimType()).get(this.ordinal - 1) != null) {
                throw new OrdinalAlreadyExistException(this.getAimType(), this.ordinal);
            } else {
                Storage.addAim(this);
            }
        } else {
            Storage.addAim(this);
        }
    }

    public AbstractAim(int ordinal) throws OrdinalAlreadyExistException,
    StorageUnexistingTypeException {
        this.dod = new ArrayList<DodPoint>();
        this.history = new ArrayList<HistoryPoint>();
        this.date = new Date();
        this.deadLine = null;
        this.status = AimStatuses.DRAFT;
        this.dateFormat = "dd.MM.yyyy";
        this.ordinal = ordinal;
        this.postmortem = null;
        this.description = null;
        this.affectedEntitiesOrdinals = null;

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

    public enum AimStatuses { DRAFT, START, CLOSE, FREEZE, UNFREEZE, MODIFY, REJECT }

    private void commonAction(AimStatuses status) {
        this.status = status;
        this.history.add(new HistoryPoint(status));
    }

    private void beforeEachAction(AimStatuses status) throws AimNotAllowedActionFlow {
        if (!allowFlows.get(status).contains(this.status)) {
            ArrayList<String> possibleStatuses = new ArrayList<>();
            for (AimStatuses oneStatus: allowFlows.get(status)) {
                possibleStatuses.add(oneStatus.toString());
            }

            throw new AimNotAllowedActionFlow(this.status.toString(), possibleStatuses);
        }
    }

    abstract public String getAimType();

    public void setState(ArrayList<String> params)
    throws EntitySetStateException, AimPostmortemWithoutCauseException, ParseException {
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        boolean callerCheck = false;

        for (StackTraceElement element: elements) {
            if (element.getMethodName().equals("loadEntities")) callerCheck = true;
        }

        if (callerCheck) {
            SimpleDateFormat format = new SimpleDateFormat(this.dateFormat);

            for (String param: params) {
                if (param.indexOf("dod | ") != -1) {
                    String theses = param.substring(param.indexOf("] ") + 2);
                    boolean status = (param.indexOf("[x]") != -1);

                    DodPoint dodPoint = new DodPoint(theses);
                    dodPoint.setStatus(status);
                    
                    this.dod.add(dodPoint);
                }
                else if (param.indexOf("deadline | ") != -1) {
                    try {
                        this.deadLine = format.parse(param.substring(param.length() - 10));
                    }
                    catch (ParseException exception) {
                        System.out.println("Aim date parsing error!\n exception is: " + exception);
                    }
                }
                else if (param.indexOf("history | ") != -1) {
                    Date date = format.parse(param.substring(param.length() - 10));
                    AimStatuses status = AimStatuses.DRAFT;

                    for (AimStatuses oneStatus: AimStatuses.values()) {
                        if (param.indexOf(oneStatus.toString()) != -1) status = oneStatus;
                    }

                    HistoryPoint historyPoint = new HistoryPoint(status);
                    historyPoint.setDate(date);
                    
                    if (status == AimStatuses.DRAFT) this.date = date;
                    if (status == AimStatuses.START) this.startDate = date;
                    if (status == AimStatuses.FREEZE) this.freezeDate = date;

                    this.history.add(historyPoint);
                    this.status = status;
                }
                else if (param.indexOf("> affected ordinals: ") != -1) {
                    this.affectedEntitiesOrdinals = new ArrayList<>();
                    String[] ordinals = param.substring(21).split(", ");

                    for (String ordinal: ordinals) {
                        this.affectedEntitiesOrdinals.add(Integer.parseInt(ordinal));
                    }
                }
                else if (param.indexOf("description | ") != -1) {
                    this.description = param.substring(14).trim();
                }
                else if (param.indexOf("postmortem | ") != -1) {
                    if (this.postmortem == null) this.postmortem = new Postmortem();

                    if (param.indexOf("# Postmortem") != -1) {
                        try {
                            this.postmortem.setDate(format.parse(param.substring(param.length() - 10)));
                        }
                        catch (ParseException exception) {
                            System.out.println(
                                "Problem with date parsing in postmortem of aim entity!\n" +
                                "Exception: " + exception
                            );
                        }
                    }
                    else if (param.indexOf("* ") == -1) {
                        this.postmortem.setConclusion(param.substring(13));
                    }
                    else {
                        this.postmortem.addCause(param.substring(15));
                    }
                }
            }

            if (this.postmortem != null) {
                if (this.postmortem.getCauses().size() == 0) {
                    throw new AimPostmortemWithoutCauseException();
                }
            }
        } else {
            throw new EntitySetStateException(this.getAimType());
        }
    }

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
    AimCloseOverdueWithoutPostmortemException, AimNotAllowedActionFlow {
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

    public void freeze() throws AimNotAllowedActionFlow {
        this.beforeEachAction(AimStatuses.FREEZE);
        
        this.freezeDate = new Date();
        this.commonAction(AimStatuses.FREEZE);
    }
    
    public void reject() throws AimRejectNotHavePostmortem, AimNotAllowedActionFlow {
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
            System.out.println("Problem with date parsing!\n" + exception.getMessage());
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

    public ArrayList<Integer> getAffectedOrdinals() {
        return this.affectedEntitiesOrdinals;
    }

    public void addAffectedOrdinal(int ordinal) {
        if (this.affectedEntitiesOrdinals == null) {
            this.affectedEntitiesOrdinals = new ArrayList<>();
        }

        this.affectedEntitiesOrdinals.add(ordinal);
    }

    public Postmortem getPostmortem() {
        return this.postmortem;
    }

    abstract public void takeAffectedEntities() throws StorageUnexistingTypeException;
    abstract public void addAffectedEntity(AbstractAim aim);
    abstract public ArrayList<AbstractAim> getAffectedEntities();
    abstract public void clearAffectedEntities();

    public class Postmortem {
        private ArrayList<String> causes;
        private String conclusion;
        private Date date;

        Postmortem() {
            this.date = new Date();
            this.causes = new ArrayList<>();
        }

        Postmortem(String conclusion, String cause, String ... moreCauses) {
            this.date = new Date();
            this.causes = new ArrayList<String>();
            this.conclusion = conclusion;
            this.causes.add(cause);
        
            for (String exCause: moreCauses) {
                this.causes.add(exCause);
            }
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public void setConclusion(String conclusion) {
            this.conclusion = conclusion;
        }

        public void addCause(String cause) {
            this.causes.add(cause);
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

    public class HistoryPoint {
        private AimStatuses status;
        private Date date;

        HistoryPoint(AimStatuses status) {
            this.status = status;
            this.date = new Date();
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public void setStatus(AimStatuses status) {
            this.status = status;
        }

        public AimStatuses getStatus() {
            return this.status;
        }

        public String getStrStatus() {
            return this.status.toString();
        }

        public Date getDate() {
            return this.date;
        }
    }

    public class DodPoint {
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

        public void setStatus(boolean status) {
            this.status = status;
        }

        public void close() {
            this.status = true;
        }
    }
}

