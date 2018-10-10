package infrastructure

import models.{Agency, Entry, Prefecture}
import scalikejdbc._

case class EntryDAO(id: Int, userId: Int, recruitmentId: Int) {
  def to(userDAO: UserDAO,
         recruitmentDAO: RecruitmentDAO,
         occupationDAO: OccupationDAO,
         storeDAO: StoreDAO,
         businessConditionDAO: BusinessConditionDAO,
         prefectureDAO: PrefectureDAO,
         areaDAO: Option[AreaDAO],
         questionDAO: Option[QuestionDAO]): Entry = {
    val store = storeDAO.to(recruitmentDAO, occupationDAO, businessConditionDAO, prefectureDAO, areaDAO)
    val user = userDAO.to()
    val question = questionDAO.map { it =>
      it.to(user)
    }
    val occupations = OccupationDAO.fetchAllOfEntry(id)
    Entry(id, user, store, question, occupations)
  }
}

object EntryDAO extends SQLSyntaxSupport[EntryDAO] {
  override val tableName = "entries"

  def apply(r: ResultName[EntryDAO])(rs: WrappedResultSet) =
    new EntryDAO(
      rs.int(r.id),
      rs.int(r.userId),
      rs.int(r.recruitmentId)
    )

  private def createSelectQuery(): SelectSQLBuilder[EntryDAO] = {
    val _entryDAO = EntryDAO.syntax
    val _recruitmentDAO = RecruitmentDAO.syntax
    val _occupationDAO = OccupationDAO.syntax
    val _userDAO = UserDAO.syntax
    val _storeDAO = StoreDAO.syntax
    val _businessConditionDAO = BusinessConditionDAO.syntax
    val _prefectureDAO = PrefectureDAO.syntax
    val _areaDAO = AreaDAO.syntax
    val _questionDAO = QuestionDAO.syntax

    select.from(EntryDAO as _entryDAO)
      .innerJoin(RecruitmentDAO as _recruitmentDAO)
      .on(_entryDAO.recruitmentId, _recruitmentDAO.id)
      .innerJoin(OccupationDAO as _occupationDAO)
      .on(_recruitmentDAO.displayOccupationId, _occupationDAO.id)
      .innerJoin(UserDAO as _userDAO)
      .on(_entryDAO.userId, _userDAO.id)
      .innerJoin(StoreDAO as _storeDAO)
      .on(_recruitmentDAO.storeId, _storeDAO.id)
      .innerJoin(BusinessConditionDAO as _businessConditionDAO)
      .on(_businessConditionDAO.id, _storeDAO.businessConditionId)
      .innerJoin(PrefectureDAO as _prefectureDAO)
      .on(_prefectureDAO.id, _storeDAO.prefectureId)
      .leftJoin(AreaDAO as _areaDAO)
      .on(_areaDAO.id, _storeDAO.areaId)
      .leftJoin(QuestionDAO as _questionDAO)
      .on(_entryDAO.id, _questionDAO.entryId)
  }

  private def createEntry(rs: WrappedResultSet): Entry = {
    val _entryDAO = EntryDAO.syntax
    val _recruitmentDAO = RecruitmentDAO.syntax
    val _occupationDAO = OccupationDAO.syntax
    val _userDAO = UserDAO.syntax
    val _storeDAO = StoreDAO.syntax
    val _businessConditionDAO = BusinessConditionDAO.syntax
    val _prefectureDAO = PrefectureDAO.syntax
    val _areaDAO = AreaDAO.syntax
    val _questionDAO = QuestionDAO.syntax

    val entryDAO = EntryDAO(_entryDAO.resultName)(rs)
    val recruitmentDAO = RecruitmentDAO(_recruitmentDAO.resultName)(rs)
    val occupationDAO = OccupationDAO(_occupationDAO.resultName)(rs)
    val userDAO = UserDAO(_userDAO.resultName)(rs)
    val storeDAO = StoreDAO(_storeDAO.resultName)(rs)
    val businessConditionDAO = BusinessConditionDAO(_businessConditionDAO.resultName)(rs)
    val prefectureDAO = PrefectureDAO(_prefectureDAO.resultName)(rs)
    val questionDAO: Option[QuestionDAO] = if (QuestionDAO.isExistOfEntryId(entryDAO.id)) {
      Some(QuestionDAO(_questionDAO.resultName)(rs))
    } else {
      None
    }
    val areaDAO: Option[AreaDAO] = if (Prefecture.hasArea(prefectureDAO.id)) {
      Some(AreaDAO(_areaDAO.resultName)(rs))
    } else {
      None
    }
    entryDAO.to(
      userDAO,
      recruitmentDAO,
      occupationDAO,
      storeDAO,
      businessConditionDAO,
      prefectureDAO,
      areaDAO,
      questionDAO)
  }

  def fetchAll()(implicit s: DBSession = AutoSession): List[Entry] = {
    val _entryDAO = EntryDAO.syntax

    withSQL {
      createSelectQuery()
        .orderBy(_entryDAO.id)
        .desc
    }.map { createEntry }.list().apply()
  }

  def fetchAllOfAgency(agency: Agency)(implicit s: DBSession = AutoSession): List[Entry] = {
    val _entryDAO = EntryDAO.syntax
    val _storeDAO = StoreDAO.syntax

    withSQL {
      createSelectQuery()
        .where
        .eq(_storeDAO.agencyId, agency.id)
        .orderBy(_entryDAO.id)
        .desc
    }.map { createEntry }.list().apply()
  }

  def fetchAllOfStoreId(storeId: Int)(implicit s: DBSession = AutoSession): List[Entry] = {
    val _entryDAO = EntryDAO.syntax
    val _storeDAO = StoreDAO.syntax

    withSQL {
      createSelectQuery()
        .where
        .eq(_storeDAO.id, storeId)
        .orderBy(_entryDAO.id)
        .desc
    }.map { createEntry }.list().apply()
  }
}