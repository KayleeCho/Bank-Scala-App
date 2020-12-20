package com.h2.entities

import java.time.LocalDate
import java.util.UUID

class Bank(val name: String,
           val city: String,
           val country: String,
           val email: Email) {
  private var depositProducts: Map[UUID, Deposits] = Map.empty
  private var depositAccounts: Map[UUID, DepositsAccount]  = Map.empty
  private var lendingProducts: Map[UUID, Lending] = Map.empty
  private var lendingAccounts: Map[UUID, LendingAccount] = Map.empty
  private var customers: Map[UUID, Customer] = Map.empty

  println(s"$name Established 2020.")

  def createNewCustomer(first: String, last: String, email: String, dateOfBirth: String): UUID = {
    def getEmail: Email = {
      val Array(value,domain) = email.split("@")
      Email(value, domain)
    }
    def getDateOfBirth: LocalDate = {
      var Array(year, month, day) = dateOfBirth.split("/")
      LocalDate.of(year.toInt, month.toInt, day.toInt)
    }
    val customer = new Customer(first, last,getEmail, getDateOfBirth)
    customers += (customer.id -> customer)
    customer.id
  }

  def addNewDepositProduct(name: String, minBalance: Int, ratePerYear: Double,
                          transactionsAllowedPerMonth: Int= 2): UUID = {
    val product = name match {
      case "CoreChecking" => new CoreChecking(Dollars(minBalance), ratePerYear)
      case "StudentCheckings" => new StudentCheckings(Dollars(minBalance), ratePerYear)
      case "RewardsSavings" => new RewardsSavings(Dollars(minBalance), ratePerYear, transactionsAllowedPerMonth)
    }
    depositProducts += (product.id -> product)
    product.id
  }

  def addNewLendingProduct(annualFee: Double, apr: Double, rewardsPercent: Double): UUID = {
    val product = new CreditCard(annualFee, apr, rewardsPercent)
    lendingProducts += (product.id -> product)
    product.id
  }

  def openDepositAccount(customerId: UUID, productId: UUID, amount: Dollars ):UUID ={
    require(customers.get(customerId).nonEmpty, s"no customer found with id=$customerId")
    require(depositProducts.get(productId).nonEmpty, s"no deposits product found with id=$productId")
    val account = new DepositsAccount(customers(customerId), depositProducts(productId), amount)
    depositAccounts += (account.id -> account)
    account.id
  }

  def openLendingAccount(customerId: UUID, productId: UUID, balance: Dollars = Dollars(0)): UUID = {
    require(customers.get(customerId).nonEmpty, s"no customer found with id =$customerId")
    require(lendingProducts.get(productId).nonEmpty, s"no lending product found with id =$customerId")

    val account = new LendingAccount (customers(customerId),lendingProducts(productId), balance )
    lendingAccounts += (account.id -> account)
    account.id
  }

  def deposit(accountId: UUID, dollars: Dollars): Unit = {
    require(depositAccounts.get(accountId).nonEmpty, "A valid deposits account Id must be provided")
    depositAccounts(accountId) deposit dollars
  }
  def withdraw(accountId: UUID, dollars: Dollars): Unit = {
    require(depositAccounts.get(accountId).nonEmpty, "A valid deposits account Id must be provided")
    depositAccounts(accountId) withdraw dollars
  }

  def useCreditCard(accountId: UUID, dollars: Dollars ): Unit = {
    require(lendingAccounts.get(accountId).nonEmpty, "A valid lending account Id must be provided")
    lendingAccounts(accountId) withdraw dollars
  }
  def payCreditCardBill(accountId: UUID, dollars: Dollars): Unit ={
    require(lendingAccounts.get(accountId).nonEmpty, "A valid lending account id must be provided")
    lendingAccounts(accountId) payBill dollars
  }

  override def toString: String = s"[$name]" +
    s" - ${customers.size} customers" +
    s" - ${depositProducts.size} deposit products" +
    s" - ${depositAccounts.size} deposits accounts" +
    s"  -${lendingProducts.size} lending products" +
    s" - ${lendingAccounts.size} lendingaccounts"

}