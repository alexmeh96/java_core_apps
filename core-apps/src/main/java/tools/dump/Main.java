package tools.dump;

//import com.sun.tools.attach.AttachNotSupportedException;
//import com.sun.tools.attach.VirtualMachine;

import sun.tools.attach.HotSpotVirtualMachine;

import java.lang.management.ManagementFactory;
import java.util.spi.ToolProvider;

public class Main {

    public static void main(String[] args) throws Exception {
        System.out.println("111");
        dump();
        System.out.println("222");


    }

//    public static void dump() throws MalformedObjectNameException, ReflectionException, InstanceNotFoundException, MBeanException {
//
//        String threadDump = (String) ManagementFactory.getPlatformMBeanServer().invoke(
//                new ObjectName("com.sun.management:type=DiagnosticCommand"),
//                "threadPrint",
//                new Object[]{},
//                new String[]{"[Ljava.lang.String;"});
//
//        System.out.println(threadDump);
//    }

//    private static String threadDump(boolean lockedMonitors, boolean lockedSynchronizers) {
//        StringBuffer threadDump = new StringBuffer(System.lineSeparator());
//        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
//        for(ThreadInfo threadInfo : threadMXBean.dumpAllThreads(lockedMonitors, lockedSynchronizers)) {
//            threadDump.append(threadInfo.toString());
//        }
//        return threadDump.toString();
//    }

    public static void dump() throws Exception {
        String vmName = ManagementFactory.getRuntimeMXBean().getName();
        System.out.println(vmName);
        String pid = vmName.substring(0, vmName.indexOf('@'));

        System.out.println(pid);

        HotSpotVirtualMachine vm = (HotSpotVirtualMachine) HotSpotVirtualMachine.attach(pid);

        try {
            vm.localDataDump();
        } finally {
            vm.detach();
        }
    }
}


