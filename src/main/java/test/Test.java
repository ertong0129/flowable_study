package test;

import liquibase.pro.packaged.T;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Test {

    public static void main(String[] args) throws Exception {
        SystemInfo si = new SystemInfo();
        HardwareAbstractionLayer hal = si.getHardware();

        GlobalMemory memory = hal.getMemory();
        long totalMemory = memory.getTotal();
        long usedMemory = totalMemory - memory.getAvailable();

        CentralProcessor processor = hal.getProcessor();
        long[] prevTicks = processor.getSystemCpuLoadTicks();
        // 睡眠1s
        TimeUnit.SECONDS.sleep(1);
        long[] ticks = processor.getSystemCpuLoadTicks();
        long nice = ticks[CentralProcessor.TickType.NICE.getIndex()] - prevTicks[CentralProcessor.TickType.NICE.getIndex()];
        long irq = ticks[CentralProcessor.TickType.IRQ.getIndex()] - prevTicks[CentralProcessor.TickType.IRQ.getIndex()];
        long softirq = ticks[CentralProcessor.TickType.SOFTIRQ.getIndex()] - prevTicks[CentralProcessor.TickType.SOFTIRQ.getIndex()];
        long steal = ticks[CentralProcessor.TickType.STEAL.getIndex()] - prevTicks[CentralProcessor.TickType.STEAL.getIndex()];
        long cSys = ticks[CentralProcessor.TickType.SYSTEM.getIndex()] - prevTicks[CentralProcessor.TickType.SYSTEM.getIndex()];
        long user = ticks[CentralProcessor.TickType.USER.getIndex()] - prevTicks[CentralProcessor.TickType.USER.getIndex()];
        long iowait = ticks[CentralProcessor.TickType.IOWAIT.getIndex()] - prevTicks[CentralProcessor.TickType.IOWAIT.getIndex()];
        long idle = ticks[CentralProcessor.TickType.IDLE.getIndex()] - prevTicks[CentralProcessor.TickType.IDLE.getIndex()];
        long totalCpu = user + nice + cSys + idle + iowait + irq + softirq + steal;

        HostInfo hostInfo = new HostInfo();
        hostInfo.setCreateTime(System.currentTimeMillis());
        hostInfo.setUsedMemory(usedMemory); // 已用内存
        hostInfo.setTotalMemory(totalMemory); // 总内存
        hostInfo.setUsedCpu(1.0-(idle * 1.0 / totalCpu)); // CPU使用率

        System.out.println(hostInfo);
        //return hostInfo;
    }
}

@Data
@Accessors(chain = true)
//@EqualsAndHashCode
class HostInfo {
    private Long createTime;
    private Long usedMemory;
    private Long totalMemory;
    private Double usedCpu;


}
