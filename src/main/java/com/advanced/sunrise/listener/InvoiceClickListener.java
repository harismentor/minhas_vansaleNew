package com.advanced.minhas.listener;

import com.advanced.minhas.model.Invoice;

/**
 * Created by mentor on 30/10/17.
 */

public interface InvoiceClickListener {
    void onInvoiceClick(Invoice invoice, int position);
}
