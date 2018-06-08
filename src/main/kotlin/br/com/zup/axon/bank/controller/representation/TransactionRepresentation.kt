package br.com.zup.axon.bank.controller.representation

import br.com.zup.axon.bank.domain.account.TransactionId


data class TransactionRepresentation(val transactionId: TransactionId)