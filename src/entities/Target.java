package entities;
    
class Target extends AbstractAim {
    private static final String AIM_TYPE = "target";

    public static String getTypeName() {
        return AIM_TYPE;
    }

    public String getAimType() {
        return AIM_TYPE;
    }
}

