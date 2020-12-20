import java.time.LocalDate
import java.util.UUID

import com.h2.entities.{Account, Bank, CoreChecking, CreditCard, Customer, DepositsAccount, Dollars, Email, LendingAccount, RewardsSavings, StudentCheckings}


object BankOfScala {
  val random = new scala.util.Random()

  def main(args: Array[String]): Unit = {
    println("Opening Bank")

    val bank = new Bank(name = "Bank of Scala", country = "New Zealand", city = "Auckland", email = Email("bank", "scala.com"))
    val customerIds = getCustomers map { c => bank.createNewCustomer(c._1, c._2, c._3, c._4) }
    val depositProductIds = getDepositProducts map { p => bank.addNewDepositProduct(p._1, p._2, p._3) }
    val lendingProductIds = getLendingProducts map { l => bank.addNewLendingProduct(l._2, l._3, l._4) }

    /* logging */
    println(s"Bank: $bank")
    println(s"CustomerIds: $customerIds")
    println(s"Deposits Products Ids: $depositProductIds")
    println(s"LendingProductIds: $lendingProductIds")

    def openAccounts(customerId: UUID, productId: UUID, productType:String ) = productType match {
      case "Deposit" => bank.openDepositAccount(customerId, productId, _: Dollars)
      case "Lending" => bank.openLendingAccount(customerId, productId, _: Dollars)
    }

    val depositAccounts = for (c <- customerIds; p <- depositProductIds) yield openAccounts(c, p, "Deposit")

    val random = new scala.util.Random()
    val depositAccountIds = depositAccounts map {account => account(Dollars(10000 + random.nextInt(100000)))}

    println(s"Deposits Accounts: $depositAccounts")
    println(s"Deposits Acount Ids: $depositAccountIds")

    val lendingAccounts = for (c <- customerIds; p <- lendingProductIds) yield openAccounts(c, p, "Lending")
    val lendingAccountIds = lendingAccounts.map(account => account(Dollars(random.nextInt(500))))

    println(s"LendingAccounts: $lendingAccounts")
    println(s"Lending Account Ids: $lendingAccountIds")
    println(s"Bank: $bank")
    val randomAmount = new scala.util.Random(100)
    depositAccountIds foreach { accountId =>
      bank deposit(accountId, Dollars(1 + randomAmount.nextInt(100)))
      bank withdraw(accountId, Dollars(1 + randomAmount.nextInt(50)))
    }

    lendingAccountIds foreach { accountId =>
      bank useCreditCard(accountId, Dollars(1 + randomAmount.nextInt(100)))
      bank payCreditCardBill (accountId, Dollars(1 + randomAmount.nextInt(50)))

    }

  }


  /* ------------------- Data ------------------- */
  def getCustomers: Seq[(String, String, String, String)] = {
    Seq(
      ("Bob", "Martin", "bob@martin.com", "1976/11/25"),
      ("Amy", "Jones", "amy.jones@google.com", "1983/4/12"),
      ("Taylor", "Jackson", "taylor33@jackson.com", "1985/4/5"),
      ("Rob", "Johnson", "rob@jphnson.com", "1976/3/6"),
      ("Maggie", "Harting", "maggie23@apple.com", "1979/12/3"),
      ("Deb", "Das", "debdas@ge.com", "1981/4/12"),
      ("Chris", "Baron", "chris@ibm.com", "1977/2/12"),
      ("Julie", "James", "julie@james.com", "1983/1/22"),
      ("Oscar", "Chin", "oscar@mama.com", "1982/4/12"),
      ("Sophie", "Smith", "sophie@fb.com", "1990/12/1")
    )
  }

  def getDepositProducts: Seq[(String, Int, Double)] = {
    Seq(
      ("CoreChecking", 1000, 0.025),
      ("StudentCheckings", 0, 0.010),
      ("RewardsSavings", 10000, 0.10),
    )
  }

  def getLendingProducts: Seq[(String, Double, Double, Double)] = {
    Seq(("CreditCard", 99.00, 14.23, 20.00))
  }
}
