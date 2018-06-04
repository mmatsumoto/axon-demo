package br.com.zup.axon.saga.controller

import br.com.zup.axon.saga.aggregate.TransactionId


data class TransactionRepresentation(val transactionId: TransactionId)