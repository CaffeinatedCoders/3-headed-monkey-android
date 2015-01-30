package net.three_headed_monkey.data;


public class PendingCommandFromApi {
    public ServiceInfo serviceInfo;
    public int id;
    public String command;
    public boolean finished;

    public PendingCommandFromApi() {
        finished = false;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!o.getClass().equals(PendingCommandFromApi.class))
            return false;
        PendingCommandFromApi pendingCommandFromApi = (PendingCommandFromApi)o;
        return pendingCommandFromApi.serviceInfo.equals(serviceInfo)
                && pendingCommandFromApi.id == id;
    }

}
