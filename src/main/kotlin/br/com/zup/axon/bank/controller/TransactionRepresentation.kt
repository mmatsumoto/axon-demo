package br.com.zup.axon.bank.controller

import br.com.zup.axon.bank.aggregate.TransactionId


data class TransactionRepresentation(val transactionId: TransactionId)