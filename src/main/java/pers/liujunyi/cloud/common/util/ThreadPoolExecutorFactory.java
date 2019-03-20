package pers.liujunyi.cloud.common.util;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/***
 * 线程池工厂类(单例模式) 执行过程是： 1）当池子大小小于corePoolSize就新建线程，并处理请求
 * 2）当池子大小等于corePoolSize，把请求放入workQueue中，池子里的空闲线程就去从workQueue中取任务并处理
 * 3）当workQueue放不下新入的任务时，新建线程入池，并处理请求，如果池子大小撑到了maximumPoolSize就用RejectedExecutionHandler来做拒绝处理
 * 4）另外，当池子的线程数大于corePoolSize的时候，多余的线程会等待keepAliveTime长的时间，如果无请求可处理就自行销毁
 * 
 * @author ljy
 */
public class ThreadPoolExecutorFactory {

    /**
     * corePoolSize 池中所保存的线程数，包括空闲线程。
     */
    private static final int corePoolSize = Runtime.getRuntime().availableProcessors();
    /**
     * maximumPoolSize - 池中允许的最大线程数(采用LinkedBlockingQueue时没有作用)。
     */
    private static final int  maximumPoolSize  = corePoolSize;
    /**
     * keepAliveTime -当线程数大于核心时，此为终止前多余的空闲线程等待新任务的最长时间，线程池维护线程所允许的空闲时间
     */
    private static final int keepAliveTime  = 100;

    /**
     * 执行前用于保持任务的队列（缓冲队列）
     */
    private static final int  capacity  = 300;

    /**
     * 线程池对象
     */
    private static ThreadPoolExecutor threadPoolExecutor = null;

    // 构造方法私有化
    private ThreadPoolExecutorFactory(){
    }

    /**
     *
     * @return
     */
    public static ThreadPoolExecutor getThreadPoolExecutor() {
        if (null == threadPoolExecutor) {
            ThreadPoolExecutor t;
            synchronized (ThreadPoolExecutor.class) {
                t = threadPoolExecutor;
                if (null == t) {
                    synchronized (ThreadPoolExecutor.class) {
                        /**
                         * corePoolSize 线程池维护线程的最少数量 maximumPoolSize 线程池维护线程的最大数量 keepAliveTime 线程池维护线程所允许的空闲时间 unit
                         * 线程池维护线程所允许的空闲时间的单位 TimeUnit.MILLISECONDS：毫秒 TimeUnit.SECONDS：秒 TimeUnit.DAYS:天
                         * TimeUnit.HOURS：小时 TimeUnit.MINUTES：分钟 workQueue 线程池所使用的缓冲队列 ArrayBlockingQueue：有界的数组阻塞队列
                         * (内部实现是将对象放到一个数组里) DelayQueue：延迟队列(对元素进行持有直到一个特定的延迟到期) LinkedBlockingQueue:链阻塞队列(内部以
                         * FIFO(先进先出)的顺序对元素进行存储) PriorityBlockingQueue:具有优先级的阻塞队列(无界的并发队列) SynchronousQueue:同步队列
                         * BlockingDeque：阻塞双端队列 LinkedBlockingDeque：链阻塞双端队列 handler 线程池对拒绝任务的处理策略
                         * ThreadPoolExecutor.AbortPolicy()：抛出java.util.concurrent.RejectedExecutionException异常
                         * ThreadPoolExecutor.CallerRunsPolicy()：当抛出RejectedExecutionException异常时，会调用rejectedExecution方法
                         * ThreadPoolExecutor.DiscardOldestPolicy()：抛弃旧的任务 ThreadPoolExecutor.DiscardPolicy()：抛弃当前的任务
                         */
                        t = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS,
                                                   new LinkedBlockingQueue<Runnable>(),
                                                   new ThreadPoolExecutor.DiscardOldestPolicy());
                    }
                    threadPoolExecutor = t;
                }
            }
        }
        return threadPoolExecutor;
    }
}
