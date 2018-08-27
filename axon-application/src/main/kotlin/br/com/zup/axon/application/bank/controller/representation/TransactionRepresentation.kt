package br.com.zup.axon.application.bank.controller.representation

import br.com.zup.axon.application.bank.domain.account.TransactionId


data class TransactionRepresentation(val transactionId: TransactionId)