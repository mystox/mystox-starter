package com.kongtrolink.framework.reports.entity;

/**
 * Created by mystoxlol on 2019/10/23, 17:30.
 * company: kongtrolink
 * description:
 * update record:
 */
public enum TaskStatus {
    VALID(1),INVALID(0),RUNNING(2),TIMEOUT(3);

    int status;
    TaskStatus(int i) {
        this.status = i;
    }


    public int getStatus() {
        return status;
    }

//    public static void main(String[] args)
//    {
//        System.out.println(TaskStatus.INVALID.getStatus());
//    }
}
