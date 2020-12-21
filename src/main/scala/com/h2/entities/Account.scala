package com.h2.entities

import java.util.UUID

sealed trait TransactionType
case object In extends TransactionType
case object Out extends TransactionType

case class Transaction(customer: Customer, amount: Dollars, transactionType: TransactionType, accountCategory: AccountCategory)
sealed trait AccountCategory
case object DepositsA extends AccountCategory
case object LendingA extends AccountCategory

abstract class Account {
  val id: UUID = UUID.randomUUID()
  val customer: Customer
  val category: AccountCategory
  val product: Product
  var transactions: Seq[Transaction] = Seq.empty
  def getBalance: Dollars
}

class DepositsAccount(val customer: Customer,
                      val product: Deposits,
                      private var balance: Dollars
                     ) extends Account {
  override val category: AccountCategory = DepositsA
  def deposit(dollars: Dollars): Unit = {
    require(dollars > Dollars(0), "amount should be greater than 0 ")
    balance += dollars
    transactions = transactions :+ Transaction(customer, dollars, In, category )
    println(s"Deposited $dollars to ${this.toString}")
  }

  def withdraw(dollars: Dollars): Unit = {
    require(dollars > Dollars(0) && balance > dollars,
    "amount should be greater than zero and requested amount should be less than and equal to the balance")
    balance -= dollars
    println(s"Withdrawn $dollars from ${this.toString}")
  }
  override def getBalance: Dollars = balance
  override def toString = s"$customer with $product has a reamining balance of $balance"
}

class LendingAccount(val customer: Customer,
                    val product: Lending,
                     private var balance: Dollars
                    ) extends Account {
  override val category: AccountCategory = LendingA
  def payBill(dollars: Dollars): Unit = {
    require(dollars  > Dollars(0), "The payment must be made for amount greater than zero ")
    balance += dollars
    println(s"Paid bill of $dollars against ${this.toString}")
  }

  def withdraw(dollars: Dollars): Unit = {
    require(dollars  > Dollars(0), "The withdrawal amount is greater than zero")
    balance -= dollars
    println(s"debitted of $dollars to ${this.toString}")
  }
  override def getBalance: Dollars = balance
  override def toString = s"$customer with $product has remaining balance of $balance "

                    }


