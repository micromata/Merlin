package de.micromata.merlin.importer;

/**
 */
public enum ImportStatus
{
  NOT_RECONCILED("notReconciled"), RECONCILED("reconciled"), HAS_ERRORS("hasErrors"), IMPORTED("imported"), NOTHING_TODO("nothingToDo");

  private String key;

  /**
   * @return The key may be used e. g. for i18n.
   */
  public String getKey()
  {
    return key;
  }

  ImportStatus(String key)
  {
    this.key = key;
  }
  
  public boolean isIn(ImportStatus... status) {
    for (ImportStatus st : status) {
      if (this == st) {
        return true;
      }
    }
    return false;
  }
}
