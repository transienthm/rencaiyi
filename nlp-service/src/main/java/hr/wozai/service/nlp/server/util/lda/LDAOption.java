package hr.wozai.service.nlp.server.util.lda;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LDAOption {

	public boolean est = false;

	public boolean estc = false;

	public boolean inf = false;

	public String dir = "";

	public String dfile = "";

	public String modelName = "";

	public double alpha = -1.0;

	public double beta = -1.0;

	public int K = 100;

	public int niters = 1000;

	public int savestep = 100;

	public int twords = 100;

	public boolean withrawdata = false;

	public String wordMapFileName = "";

}
