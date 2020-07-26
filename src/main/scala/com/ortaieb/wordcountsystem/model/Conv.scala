package com.ortaieb.wordcountsystem.model

/**
  */
trait Conv[I, O] {
  def conv(raw: I): O
}

object Conv {
  implicit val countedRecordConf: Conv[RawRecord, CountedRecord] =
    new Conv[RawRecord, CountedRecord] {
      override def conv(raw: RawRecord): CountedRecord = CountedRecord(raw)
    }
}
