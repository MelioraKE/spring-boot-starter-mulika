package tech.meliora.mulika.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tech.meliora.mulika.domain.enumerations.ServiceType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceStats {
    private ServiceType type;

    private String name;

    private long totalRequests;

    private long successTotal;

    private long rejectedMessages;

    private int queueSize;

    private int transactionTime;

    public int getAvgTransactionTime() {
        if (totalRequests == 0) {
            return 0;
        }
        return (int) (transactionTime / totalRequests);
    }

    public void addRequest(boolean successful, int transactionTime) {
        this.totalRequests++;
        if (successful) {
            this.successTotal++;
        }
        this.transactionTime += transactionTime;
    }

    public void resetCounters() {
        this.totalRequests = 0;
        this.successTotal = 0;
        this.rejectedMessages = 0;
        this.queueSize = 0;
        this.transactionTime = 0;
    }

    @Override
    public String toString() {
        return "ServiceStats{" +
                "name='" + name + '\'' +
                ", totalRequests=" + totalRequests +
                ", successTotal=" + successTotal +
                ", rejectedMessages=" + rejectedMessages +
                ", queueSize=" + queueSize +
                ", transactionTime=" + transactionTime +
                '}';
    }
}

 