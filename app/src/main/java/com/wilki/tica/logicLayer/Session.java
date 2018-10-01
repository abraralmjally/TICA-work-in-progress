package com.wilki.tica.logicLayer;

import android.content.Context;

import com.wilki.tica.dataLayer.DbHelper;

import java.util.ArrayList;
import java.util.List;

public class Session {

    private int SessionID;
    private String GroupID;
    private boolean Sessionavailable;
    private static List <TaskPerformance> TPlist;
    private int TPlistsize;
    private TaskPerformance currentTaskPerformance;


    /**
     * Constructor used to create a fresh Session

     * @param GroupID to linke the session with the group name
     */
    public Session (String GroupID)

    {
        this.GroupID=GroupID;
        TPlist= new ArrayList<>();
        Sessionavailable=false;
        SessionID=-1;
    }


    public Session(String groupId, List<TaskPerformance> TPlist) {
        this.GroupID=groupId;
        this.TPlist = TPlist;
        Sessionavailable=false;
    }

    /**
     * Constructor used to re-create a Session that has previously been saved to a
    * database.
    * @param sessionid session unique identification number.
    * @Parm  groupId the unique group identification number.
     */
    public Session(int sessionid, String groupId, int TPlistsize) {
    this.SessionID=sessionid;
    this.GroupID=groupId;
    this.TPlistsize = TPlistsize;
    Sessionavailable=true;
    }

    public static void saveSession(Context cont, Session session) {
    DbHelper dbHelper = new DbHelper(cont);
    dbHelper.addEntryToSession(session);
    dbHelper.close();
    }
/*
    public static void UpdateSession(Context cont, String sessionID,List<TaskPerformance> TPlist ) {
        DbHelper dbHelper = new DbHelper(cont);
        dbHelper.UpdateSession( sessionID, TPlist);
        dbHelper.close();
    }*/

    public static List <Session> getAllSessions(Context cont){
        DbHelper dbHelper = new DbHelper(cont);
        List<Session> sessions = dbHelper.readAllFormSessionTable();
        dbHelper.close();
        return sessions;
    }

    public static void AddTaskPerformance(TaskPerformance TP)
    {
        TPlist.add(TP);
    }

    /**
     * Deletes all Sessions data from the database.
     * @param cont the application context.
     */
    public static void deleteAllSessionData(Context cont){
        DbHelper dbHelper = new DbHelper(cont);
        dbHelper.resetSessionTable(dbHelper.getWritableDatabase());
        dbHelper.close();
    }

    public int getSessionID(){return SessionID;}
    public boolean isAvailable (){ return Sessionavailable;}
    public  void SetSessionID(int sessionID){
        SessionID=sessionID;
    }

    public String  getGroupID() { return GroupID; }
    public List<TaskPerformance> getTaskPerformanceList (){return TPlist;}

    public void setTPlist (Context cont, Session session)
    {
        DbHelper dbHelper = new DbHelper(cont);
        List<TaskPerformance> performances =dbHelper.readSelectedFromTaskTPTable(session.getSessionID());
        TPlist=performances;
        dbHelper.close();

    }


    public int getNumPerformance(){ return TPlist.size(); }

    //TODO
   // public void SartNewPserformance () {currentTaskPerformance = new TaskPerformance();}




    public Session findSession (Context cont , String GName) {
        List <Session> AllSessions= getAllSessions( cont);

        for(Session session: AllSessions){

            if(GName.equals(session.getGroupID()))
            {
                Sessionavailable=false;
                return session;
            }
        }
        Sessionavailable=false;
        return null;
    }


}
