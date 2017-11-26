package hr.wozai.service.user.server.service;

import hr.wozai.service.user.server.model.okr.KeyResult;
import hr.wozai.service.user.server.model.okr.Objective;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/3/17
 */
public interface OkrLogService {
  public void insertOkrLogOfCreateObjective(Objective objective);

  public void insertOkrLogOfDeleteObjective();

  public void insertOkrLogOfCreateKeyResult(KeyResult keyResult);

  public void insertOkrLogOfDeleteKeyResult();

  public void insertOkrLogOfUpdateObjective(Objective objective);

  public void insertOkrLogOfUpdateKeyResult(KeyResult keyResult);
}
