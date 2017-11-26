package hr.wozai.service.servicecommons.utils.monitor;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClient;
import com.amazonaws.services.cloudwatch.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Created by pujiule on 11/21/15.
 */
public class AwsClient {

    private static final String MONITOR_FILE = "monitor.properties";

    private AmazonCloudWatchClient client;
    private Properties properties;

    public AwsClient(Properties properties) {
        this.properties = properties;
        String accessKey = properties.getProperty("aws.access.key");
        String secretKey = properties.getProperty("aws.secret.key");
        String endpoint = properties.getProperty("cloudwatch.endpoint");
        client = new AmazonCloudWatchClient(new BasicAWSCredentials(accessKey, secretKey));
        client.setEndpoint(endpoint);
        preCall();
    }

    private void preCall() {
        MetricDatum metricDatum = new MetricDatum()
                .withMetricName("precall.count")
                .withTimestamp(new Date())
                .withUnit(StandardUnit.Count)
                .withValue((double)1l);
        PutMetricDataRequest putMetricDataRequest = new PutMetricDataRequest()
                .withMetricData(metricDatum);
        putMetricDataRequest.setNamespace(properties.getProperty("monitor.namespace", "GetStarted"));
        client.putMetricData(putMetricDataRequest);
    }

    public void putDataToAws(Map<String, Item> map) {
        List<MetricDatum> metricDatumList = new ArrayList<MetricDatum>();
        for (Map.Entry<String, Item> entry : map.entrySet()) {
            Item item = entry.getValue();
            if (item.getCount() != 0) {
                metricDatumList.add(getDatum(entry.getKey(), item));
                // metricDatumList.add(getCountDatum(entry.getKey(), item.getCount()));
                // metricDatumList.add(getAvgTimeDatum(entry.getKey(), item.getSumtime()/item.getCount()));
            }
            if (metricDatumList.size() >= 20) {
                doPut(metricDatumList);
                metricDatumList = new ArrayList<MetricDatum>();
            }

        }
        if (!metricDatumList.isEmpty()) {
            doPut(metricDatumList);
        }
    }

    public void doPut(List<MetricDatum> metricDatums) {
        PutMetricDataRequest putMetricDataRequest = new PutMetricDataRequest()
                .withMetricData(metricDatums);
        putMetricDataRequest.setNamespace(properties.getProperty("monitor.namespace", "GetStarted"));
        client.putMetricData(putMetricDataRequest);
    }

    public MetricDatum getCountDatum(String key, int count) {
        MetricDatum metricDatum = new MetricDatum()
                .withMetricName(key + ".count")
                .withTimestamp(new Date())
                .withUnit(StandardUnit.Count)
                .withValue((double)count);
        return metricDatum;
    }

    public MetricDatum getAvgTimeDatum(String key, long ts) {
        MetricDatum metricDatum = new MetricDatum()
                .withMetricName(key + ".avg.time")
                .withTimestamp(new Date())
                .withUnit(StandardUnit.Milliseconds)
                .withValue((double)ts);
        return metricDatum;
    }

    public MetricDatum getDatum(String key, Item item) {
        double maxTime = item.getMaxTime();
        double minTime = item.getMinTime();
        double sumTime = item.getSumtime();
        double count = item.getCount();
        MetricDatum metricDatum = new MetricDatum()
                .withMetricName(key)
                .withTimestamp(new Date())
                .withUnit(StandardUnit.Milliseconds)
                .withStatisticValues(new StatisticSet().withMaximum(maxTime).withMinimum(minTime)
                        .withSum(sumTime).withSampleCount(count));
        return metricDatum;
    }
}
