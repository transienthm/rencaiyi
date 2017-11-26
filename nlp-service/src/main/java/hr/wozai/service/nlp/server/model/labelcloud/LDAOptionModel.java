package hr.wozai.service.nlp.server.model.labelcloud;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LDAOptionModel {

  private boolean est;

  private boolean estc;

  private boolean inf;

  private String dir;

  private String dfile;

  private String modelName;

  private double alpha;

  private double beta;

  private int K;

  private int niters;

  private int savestep;

  private int twords;

  private boolean withrawdata;

  private String wordMapFileName;

  private double weightThreshold;

  private long maxTopicNumber;

  public boolean getEst() {
    return this.est;
  }

  public boolean getEstc() {
    return this.estc;
  }

  public boolean getInf() {
    return this.inf;
  }

  public boolean getWithrawdata() {
    return this.withrawdata;
  }

}
