/**
 * Copyright (c) 2016 WOZAI Inc.
 * All rights reserved.
 *
 * "Label Cloud Scheduled Task" version 1.0
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *    * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *    * Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following disclaimer
 * in the documentation and/or other materials provided with the
 * distribution.
 *    * Neither the name of Wozai Inc. nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * ---
 * @Author:  Zich Liu
 * @Version: 1.0
 * @Created: 2016-11-26 12:20:00
 * @E-mail:  liuzhenfu@sqian.com
 *
 * ---
 * Description:
 *   Label-Cloud-Scheduled-Task.
 *
 * ---
 * TODO:
 *   1. 人工干预 最终标签云 结果;
 *   2. 人工干预 分词 词典;
 *   3. 人工干预 停用词 词典;
 *   4. 人工干预 LDA 训练样本.
 */

package hr.wozai.service.nlp.server.task.labelcloud;

import java.util.Map;
import java.util.List;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.alibaba.fastjson.JSONObject;

import org.springframework.util.CollectionUtils;
import org.springframework.stereotype.Component;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import hr.wozai.service.servicecommons.utils.logging.LogAround;

import hr.wozai.service.nlp.server.util.lda.Pair;
import hr.wozai.service.nlp.server.util.lda.Estimator;
import hr.wozai.service.nlp.server.util.lda.Inferencer;
import hr.wozai.service.nlp.server.model.labelcloud.LDAOptionModel;

import hr.wozai.service.nlp.server.util.segment.SegToken;
import hr.wozai.service.nlp.server.util.segment.JiebaSegmenter;
import hr.wozai.service.nlp.server.util.segment.JiebaSegmenter.SegMode;

import hr.wozai.service.nlp.server.model.labelcloud.LabelCloudModel;
import hr.wozai.service.nlp.server.model.labelcloud.SurveyResponseModel;
import hr.wozai.service.nlp.server.service.inter.labelcloud.LabelCloudService;

/**
 * @Author: Zich Liu
 * @Version: 1.0
 * @Created: 2016-11-26 12:20:00
 *
 * 逻辑说明:
 *     第一步: 从数据库中, 将所有的 Comments 全部提取出来, 根据 orgId 以及 surveyActivityId 将这些 Comments 分组,
 *            存储到 HashMap<orgId, HashMap<surveyActivityId, List<Comment>>> allCommentsMap 中;
 *     第二步: 遍历 allCommentsMap 中的每一个 Comment, 经由分词器格式化为空格隔开的新 Comment, 并覆盖原来值;
 *     第三步: 将 allCommentsMap 中所有的 Comments 作为训练样本数据, 得到训练模型;
 *     第四步: 根据得到的上述模型, 用以预测 allCommentsMap 中的每一项, 得到最终的标签云结果, 存储到
 *            HashMap<orgId, HashMap<surveyActivityId, HashMap<label, weight>>> labelClouds 中;
 *     第五步: 遍历 labelClouds, 并依次将结果存储到数据中.
 *
 *     TODO: ldaoption -> bean; 权重归一化; 最终结果人工干预, 分词人工干预, 停用词人工干预
 */
@Component
public class LabelCloudScheduledTask {

  //*******************************************************************************************************************

  // 全局的"日志记录器"
  private Logger logger = LoggerFactory.getLogger(LabelCloudScheduledTask.class);

  //*******************************************************************************************************************

  @Autowired
  private LabelCloudService labelCloudService;

  private static final String STOP_WORDS_DICT = "/segment/stop_words.txt";

  @Autowired
  @Qualifier("ldaOptionForEstimator")
  private LDAOptionModel ldaOptionForEstimator;

  @Autowired
  @Qualifier("ldaOptionForInferencer")
  private LDAOptionModel ldaOptionForInferencer;

  private Estimator estimator;
  private Inferencer inferencer;

  private JiebaSegmenter segmenter;

  private HashSet stopWordsSet;
  private Long newCloudVersion;

  private List<String> allTrainData;
  private List<LabelCloudModel> allLabelClouds;
  private HashMap<Long, HashMap<Long, HashMap<Long, List<String>>>> allForecastData;

  @LogAround
  private void loadStopWords() throws Exception {
    InputStream resource = this.getClass().getResourceAsStream(this.STOP_WORDS_DICT);
    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(resource));
    while (true) {
      String line = bufferedReader.readLine();
      if (line == null) {
        break;
      }
      this.stopWordsSet.add(line);
    }
    bufferedReader.close();
    resource.close();
  }

  @LogAround
  private void reset() {
    this.newCloudVersion = -1L;

    this.stopWordsSet = new HashSet();

    this.estimator = new Estimator();
    this.inferencer = new Inferencer();

    this.segmenter = new JiebaSegmenter();

    this.allTrainData = new ArrayList<>();
    this.allForecastData = new HashMap<>();
    this.allLabelClouds = new ArrayList<>();
  }

  @LogAround
  private void init() throws Exception {
    this.reset();
    this.loadStopWords();
  }

  @LogAround
  private void clear() {
    this.estimator = new Estimator();
    this.inferencer = new Inferencer();

    this.segmenter = new JiebaSegmenter();

    this.newCloudVersion = -1L;
    this.stopWordsSet = new HashSet();

    this.allTrainData = new ArrayList<>();
    this.allForecastData = new HashMap<>();
    this.allLabelClouds = new ArrayList<>();
  }

  @LogAround
  private void __insertToHashMapList(
          long orgId,
          long surveyActivityId,
          long surveyItemId,
          String value,
          HashMap<Long, HashMap<Long, HashMap<Long, List<String>>>> hashMapList
  ) throws Exception {
    if (hashMapList.containsKey(orgId)) {
      HashMap<Long, HashMap<Long, List<String>>> surveyActivityIdToValues = hashMapList.get(orgId);
      if (surveyActivityIdToValues.containsKey(surveyActivityId)) {
        HashMap<Long, List<String>> surveyItemIdToValues = surveyActivityIdToValues.get(surveyActivityId);
        if (surveyItemIdToValues.containsKey(surveyItemId)) {
          surveyItemIdToValues.get(surveyItemId).add(value);
        } else {
          surveyItemIdToValues.put(
                  surveyItemId,
                  new ArrayList<>(
                          Arrays.asList(
                                  value
                          )
                  )
          );
        }
      } else {
        surveyActivityIdToValues.put(
                surveyActivityId,
                new HashMap() {{
                    put(
                            surveyItemId,
                            new ArrayList<>(
                                    Arrays.asList(value)
                            )
                    );
                }}
        );
      }
    } else {
      hashMapList.put(
              orgId,
              new HashMap() {{
                  put(
                          surveyActivityId,
                          new HashMap() {{
                              put(
                                      surveyItemId,
                                      new ArrayList<>(
                                              Arrays.asList(value)
                                      )
                              );
                          }}
                  );
              }}
      );
    }
  }

  @LogAround
  private String __wordSegment(String sentence) throws Exception {
    String segmentedWords = "";
    if (sentence != null && !sentence.isEmpty()) {
      List<SegToken> segTokens = this.segmenter.process(sentence, SegMode.SEARCH);
      if (!CollectionUtils.isEmpty(segTokens)) {
        for (SegToken segToken : segTokens) {
          if (stopWordsSet.contains(segToken.word)) {
            continue;
          }
          if (!segmentedWords.isEmpty()) {
            segmentedWords += " ";
          }
          segmentedWords += segToken.word;
        }
        segmentedWords = segmentedWords.trim();
      }
    }
    return segmentedWords;
  }

  @LogAround
  private boolean checkTrigger() throws Exception {
    Integer isDeleted = this.labelCloudService.getIsDeleted();
    Long maxSurveyResponseId = this.labelCloudService.getMaxSurveyResponseId();
    Long currentCloudVersion = this.labelCloudService.getCurrentCloudVersion();

    return maxSurveyResponseId > currentCloudVersion && isDeleted == 0;
  }

  @LogAround
  private void getTrainData() throws Exception {
    List<SurveyResponseModel> surveyResponseModels = this.labelCloudService.getAllSurveyResponses();
    if (!CollectionUtils.isEmpty(surveyResponseModels)) {
      this.newCloudVersion = surveyResponseModels.get(surveyResponseModels.size() - 1).getSurveyResponseId();

      for (SurveyResponseModel item : surveyResponseModels) {
        String responseDetail = item.getResponseDetail();
        if (responseDetail == null) {
          continue;
        }

        responseDetail = this.__wordSegment(responseDetail);
        if (responseDetail.isEmpty()) {
          continue;
        }

        this.allTrainData.add(responseDetail);

        this.__insertToHashMapList(
                item.getOrgId(),
                item.getSurveyActivityId(),
                item.getSurveyItemId(),
                responseDetail,
                this.allForecastData
        );
      }
    } else {
      throw new Exception("No survey responses got !");
    }
  }

  @LogAround
  private void train() throws Exception {
    if (!CollectionUtils.isEmpty(this.allTrainData)) {
      this.estimator.init(this.ldaOptionForEstimator, this.allTrainData);
      this.estimator.estimate();
    } else {
      throw new Exception("No valid train data !");
    }
  }

  @LogAround
  private JSONObject __packLabelCloud(Inferencer inferencer) {
    JSONObject result = new JSONObject();

    if (inferencer.newModel.twords > inferencer.newModel.V) {
      inferencer.newModel.twords = inferencer.newModel.V;
    }

    HashMap totalResultsMap = new HashMap<String, Double>();
    for (int k = 0; k < inferencer.newModel.K; k++){
      List<Pair> wordsProbsList = new ArrayList<>();
      for (int v = 0; v < inferencer.newModel.V; v++){
        Pair pair = new Pair(v, inferencer.newModel.phi[k][v], false);

        wordsProbsList.add(pair);
      }

      Collections.sort(wordsProbsList);

      for (int i = 0; i < inferencer.newModel.twords; i++){
        if (inferencer.newModel.data.localDict.contains((Integer)wordsProbsList.get(i).first)){
          String label = inferencer.newModel.data.localDict.getWord((Integer)wordsProbsList.get(i).first);
          Double weight = (Double) wordsProbsList.get(i).second;
          if (totalResultsMap.containsKey(label)) {
            totalResultsMap.put(label, (Double)totalResultsMap.get(label) + weight);
          } else {
            totalResultsMap.put(label, weight);
          }
        }
      }
    }

    if (!CollectionUtils.isEmpty(totalResultsMap)) {
      List<Pair> totalResultsList = new ArrayList<>();

      Iterator entries = totalResultsMap.entrySet().iterator();
      while (entries.hasNext()) {
        Map.Entry entry = (Map.Entry) entries.next();

        String key = (String) entry.getKey();
        Double value = (Double) entry.getValue();

        totalResultsList.add(new Pair(key, value));
      }

      Collections.sort(totalResultsList);

      for (Pair pair : totalResultsList) {
        String label = pair.first.toString();
        Comparable weight = pair.second;

        if ((Double) weight < this.ldaOptionForInferencer.getWeightThreshold()) {
          continue;
        }

        if (result.size() > this.ldaOptionForInferencer.getMaxTopicNumber()) {
          break;
        }

        result.put(label, weight.toString());
      }
    }

    return result;
  }

  @LogAround
  private void forecast() throws Exception {
    this.inferencer.init(this.ldaOptionForInferencer, this.estimator);

    for (Long orgId : this.allForecastData.keySet()) {
      HashMap<Long, HashMap<Long, List<String>>> surveyActivityIdToValues = this.allForecastData.get(orgId);

      for (Long surveyActivityId : surveyActivityIdToValues.keySet()) {
        HashMap<Long, List<String>> surveyItemIdToValues = surveyActivityIdToValues.get(surveyActivityId);

        for (Long surveyItemId : surveyItemIdToValues.keySet()) {
          List<String> forecastData = surveyItemIdToValues.get(surveyItemId);

          if (CollectionUtils.isEmpty(forecastData)) {
            continue;
          }

          this.inferencer.inference(forecastData);
          JSONObject labelCloud = this.__packLabelCloud(this.inferencer);

          if (!CollectionUtils.isEmpty(labelCloud)) {
            LabelCloudModel labelCloudModel = new LabelCloudModel();
            labelCloudModel.setOrgId(orgId);
            labelCloudModel.setSurveyActivityId(surveyActivityId);
            labelCloudModel.setSurveyItemId(surveyItemId);
            labelCloudModel.setCloudVersion(this.newCloudVersion);
            labelCloudModel.setLabelCloud(labelCloud);

            this.allLabelClouds.add(labelCloudModel);
          }
        }
      }
    }
  }

  @LogAround
  private void writeResultsToDatabase() throws Exception {
    if (this.newCloudVersion >= 0 && !CollectionUtils.isEmpty(this.allLabelClouds)) {
      this.labelCloudService.batchInsertLabelClouds(this.allLabelClouds);
      this.labelCloudService.updateCloudVersion(this.newCloudVersion);
    } else {
      throw new Exception("No valid data to write !");
    }
  }

  // LabelCloudScheduledTask 类的核心处理函数
  @LogAround
  @Scheduled(cron="0 0 0 * * ?")
  public void run() {

    this.logger.info("Label-Cloud-Scheduled-Task start ... ...");

    try {
      if (this.checkTrigger()) {
        this.init();
        this.getTrainData();
        this.train();
        this.forecast();
        this.writeResultsToDatabase();
      }
    } catch (Exception e) {
      this.logger.error("LabelCloudScheduledTask-run(): error ", e);
    } finally {
      this.clear();
    }

    this.logger.info("Label-Cloud-Scheduled-Task finished!");

  } // Class-Function-End: run()

} // Class-End: LabelCloudScheduledTask
