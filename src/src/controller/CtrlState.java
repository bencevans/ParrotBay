package controller;
public enum CtrlState
{
    DEFAULT, INIT, LANDED, FLYING, HOVERING, TEST, TRANS_TAKEOFF, TRANS_GOTOFIX, TRANS_LANDING;

    public static CtrlState fromInt(int v)
    {
        switch(v)
        {
        case 0:
            return DEFAULT;
        case 1:
            return INIT;
        case 2:
            return LANDED;
        case 3:
            return FLYING;
        case 4:
            return HOVERING;
        case 5:
            return TEST;
        case 6:
            return TRANS_TAKEOFF;
        case 7:
            return TRANS_GOTOFIX;
        case 8:
            return TRANS_LANDING;
        default:
            System.out.println("Invalid control state " + v);
            System.exit(1);
            return DEFAULT;
        }
    }
}