package com.zhihao.p2p_server;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class test extends Thread {
    //本实例中只是为了告诉大家具有返回值的线程是简单demo
    //想要深入了解以及实现的机制以及原理可以自己学习或者研究源代码
    //也可以在我博客中@Kyunchen
    public static void main(String[] args) {
        //首先创建执行线程的线程池,
        ExecutorService exs = Executors.newCachedThreadPool();
        CallableThread ct = new CallableThread();//实例化任务对象
        //大家对Future对象如果陌生，说明你用带返回值的线程用的比较少，要多加练习
        Future<Object> future = exs.submit(ct);//使用线程池对象执行任务并获取返回对象
        try {

            Object sum = future.get();//当调用了future的get方法获取返回的值得时候
            //如果线程没有计算完成，那么这里就会一直阻塞等待线程执行完成拿到返回值
            System.out.println(sum);
            System.out.println("等待？");
        } catch (Exception e) {
            e.printStackTrace();
        }
        exs.shutdown();
    }
}
class CallableThread implements Callable<Object> {
    @Override
    public Object call() throws Exception {
        //此处使用计算一到十的和然后返回
        int sum = 0;
        for(int i = 1;i<=10;i++){
            System.out.println(Thread.currentThread().getName());
            sum += i;
        }
        return sum;
    }
}

