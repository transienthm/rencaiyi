package hr.wozai.service.nlp.client.labelcloud.dto;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import hr.wozai.service.servicecommons.thrift.model.BaseThriftObject;

@ThriftStruct
public final class LabelDTO extends BaseThriftObject implements Comparable<LabelDTO> {

    private String label;
    private String weight;

    @ThriftField(1)
    public String getLabel() {
        return this.label;
    }

    @ThriftField(2)
    public String getWeight() {
        return this.weight;
    }

    @ThriftField
    public void setLabel(String label) {
        this.label = label;
    }

    @ThriftField
    public void setWeight(String weight) {
        this.weight = weight;
    }

    @Override
    public int compareTo(LabelDTO another) {
        if (Double.parseDouble(this.weight) > Double.parseDouble(another.weight)) {
            return 1;
        }
        if (Double.parseDouble(this.weight) < Double.parseDouble(another.weight)) {
            return -1;
        }
        return 0;
    }
}