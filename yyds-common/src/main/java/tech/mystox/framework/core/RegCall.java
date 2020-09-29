package tech.mystox.framework.core;

public interface RegCall {
    // public  final static int Unknown=-1;
    // public  final static int Disconnected=0;
    // public  final static int NoSyncConnected=1;
    // public  final static int SyncConnected=3;
    // public  final static int AuthFailed=4;
    // public  final static int ConnectedReadOnly=5;
    // public  final static int SaslAuthenticated=6;
    // public  final static int RebuildStatus =10;
    // public  final static int Expired=-112;
    public void call(RegState code) throws InterruptedException;



    public enum RegState {
        Unknown(-1),
        Disconnected(0),
        NoSyncConnected(1),Closed(2),
        SyncConnected(3),
        AuthFailed(4),
        ConnectedReadOnly(5),
        SaslAuthenticated(6),
        Unregister(7),
        RebuildStatus(10),
        Expired(-112);

        private int state;
        RegState(int state) {
            this.state = state;
        }

        public int getState() {
            return state;
        }

        public static RegState fromInt(int intValue) {
            switch(intValue) {
                case -1: return RegState.Unknown;
                case  -112: return RegState.Expired;
                case  0: return RegState.Disconnected;
                case  1: return RegState.NoSyncConnected;
                case  2: return RegState.Closed;
                case  3: return RegState.SyncConnected;
                case  4: return RegState.AuthFailed;
                case  5: return RegState.ConnectedReadOnly;
                case  6: return RegState.SaslAuthenticated;
                case  7: return RegState.Unregister;
                case  10: return RegState.RebuildStatus;
                default:
                    throw new RuntimeException("Invalid integer value for conversion to RegState");
            }
        }





        // public static void main(String[] args)
        // {
        //     System.out.println(RegisterState.AuthFailed.getState());
        // }
    }
}
