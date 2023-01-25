package com.advanced.minhas.webservice;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.advanced.minhas.config.ConfigURL;
import com.advanced.minhas.controller.ApplicationController;

import org.json.JSONException;
import org.json.JSONObject;

import static com.advanced.minhas.config.ConfigURL.URL_CONTRA_VOUCHER;
import static com.advanced.minhas.config.ConfigURL.URL_DAY_CLOSE;
import static com.advanced.minhas.config.ConfigURL.URL_GETLEDGER;
import static com.advanced.minhas.config.ConfigURL.URL_GET_ALL_PENDING_INVOICES;
import static com.advanced.minhas.config.ConfigURL.URL_GET_ALL_RECEIPT;
import static com.advanced.minhas.config.ConfigURL.URL_GET_CASHIN_HAND;
import static com.advanced.minhas.config.ConfigURL.URL_GROUPS;
import static com.advanced.minhas.config.ConfigURL.URL_GROUPS_REGISTER;
import static com.advanced.minhas.config.ConfigURL.URL_NO_SALE;
import static com.advanced.minhas.config.ConfigURL.URL_MULTIPLE_RETURN;
import static com.advanced.minhas.config.ConfigURL.URL_ORDER_TRANSFER_DETAILS;
import static com.advanced.minhas.config.ConfigURL.URL_SALE_RETURN;
import static com.advanced.minhas.config.ConfigURL.URL_STOCK_FETCH_LIVE;
import static com.advanced.minhas.config.ConfigURL.URL_STOCK_TRANSFER;
import static com.advanced.minhas.config.ConfigURL.URL_STOCK_TRANSFER_APPROVE;
import static com.advanced.minhas.config.ConfigURL.URL_STOCK_TRANSFER_DETAILS;
import static com.advanced.minhas.config.ConfigURL.URL_UPDATE_CUSTOMER;
import static com.advanced.minhas.config.ConfigURL.URL_VAN_TO_WAREHOUSE_APPROVE;

/**
 * Created by mentor on 1/6/17.
 */

public class WebService {

    public WebService() {
    }

    /**
     * Login Web Server using Volley
     */
    public static void webLogin(final webObjectCallback callback, JSONObject object) {

        final String REQ_TAG = "volley_login_tag";

        Log.v("","post Login URL -"+ConfigURL.URL_LOGIN);

        JsonObjectRequest loginRequest = new JsonObjectRequest(Request.Method.POST, ConfigURL.URL_LOGIN, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                callback.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                String errorMessage = null;

                if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                    if (error.getClass().equals(TimeoutError.class)) {
                        // Show timeout error message
                        errorMessage = "Oops. Timeout error!";

                    } else {
                        errorMessage = "Oops. NoConnectionError!";
                    }

                } else if (error instanceof AuthFailureError) {

                    errorMessage = "Oops. AuthFailureError!";
                } else if (error instanceof ServerError) {

                    errorMessage = "Oops. ServerError!";
                } else if (error instanceof NetworkError) {
                    errorMessage = "Oops. NetworkError!";
                } else if (error instanceof ParseError) {
                    errorMessage = "Oops. ParseError!";
                }


                callback.onErrorResponse(errorMessage);

            }
        });

        // Adding request to request queue
        ApplicationController.getInstance().addToRequestQueue(loginRequest, REQ_TAG);

    }

    /**
     * GroupList Web Server using Volley
     */
    public static void webGroupList(final webObjectCallback callback, JSONObject object) {

        final String REQ_TAG = "volley_login_tag";

        JsonObjectRequest loginRequest = new JsonObjectRequest(Request.Method.POST, URL_GROUPS, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                callback.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                String errorMessage = null;

                if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                    if (error.getClass().equals(TimeoutError.class)) {
                        // Show timeout error message
                        errorMessage = "Oops. Timeout error!";

                    } else {
                        errorMessage = "Oops. NoConnectionError!";
                    }

                } else if (error instanceof AuthFailureError) {

                    errorMessage = "Oops. AuthFailureError!";
                } else if (error instanceof ServerError) {

                    errorMessage = "Oops. ServerError!";
                } else if (error instanceof NetworkError) {
                    errorMessage = "Oops. NetworkError!";
                } else if (error instanceof ParseError) {
                    errorMessage = "Oops. ParseError!";
                }
                callback.onErrorResponse(errorMessage);
            }
        });

        // Adding request to request queue
        ApplicationController.getInstance().addToRequestQueue(loginRequest, REQ_TAG);

    }



    /**
     * Get ledger rprtServer using Volley
     */
    public static void webGetLedger(final webObjectCallback callback, JSONObject object) {

        final String REQ_TAG = "volley_login_tag";

        JsonObjectRequest loginRequest = new JsonObjectRequest(Request.Method.POST, URL_GETLEDGER, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                callback.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                String errorMessage = null;

                if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                    if (error.getClass().equals(TimeoutError.class)) {
                        // Show timeout error message
                        errorMessage = "Oops. Timeout error!";

                    } else {
                        errorMessage = "Oops. NoConnectionError!";
                    }

                } else if (error instanceof AuthFailureError) {

                    errorMessage = "Oops. AuthFailureError!";
                } else if (error instanceof ServerError) {

                    errorMessage = "Oops. ServerError!";
                } else if (error instanceof NetworkError) {
                    errorMessage = "Oops. NetworkError!";
                } else if (error instanceof ParseError) {
                    errorMessage = "Oops. ParseError!";
                }
                callback.onErrorResponse(errorMessage);
            }
        });

        // Adding request to request queue
        ApplicationController.getInstance().addToRequestQueue(loginRequest, REQ_TAG);

    }


    /**
     * Group Register Server using Volley
     */
    public static void webGroupRegister(final webObjectCallback callback, JSONObject object) {

        final String REQ_TAG = "volley_group_register_tag";

        JsonObjectRequest loginRequest = new JsonObjectRequest(Request.Method.POST, URL_GROUPS_REGISTER, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                callback.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                String errorMessage = null;

                if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                    if (error.getClass().equals(TimeoutError.class)) {
                        // Show timeout error message
                        errorMessage = "Oops. Timeout error!";

                    } else {
                        errorMessage = "Oops. NoConnectionError!";
                    }

                } else if (error instanceof AuthFailureError) {

                    errorMessage = "Oops. AuthFailureError!";
                } else if (error instanceof ServerError) {

                    errorMessage = "Oops. ServerError!";
                } else if (error instanceof NetworkError) {
                    errorMessage = "Oops. NetworkError!";
                } else if (error instanceof ParseError) {
                    errorMessage = "Oops. ParseError!";
                }
                callback.onErrorResponse(errorMessage);
            }
        });

        // Adding request to request queue
        ApplicationController.getInstance().addToRequestQueue(loginRequest, REQ_TAG);

    }

    /**
     * Day Close Server using Volley
     */
    public static void webDayClose(final webObjectCallback callback, JSONObject object) {

        final String REQ_TAG = "volley_day_close_tag";

        JsonObjectRequest dayCloseRequest = new JsonObjectRequest(Request.Method.POST, URL_DAY_CLOSE, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                callback.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                String errorMessage = null;

                if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                    if (error.getClass().equals(TimeoutError.class)) {
                        // Show timeout error message
                        errorMessage = "Oops. Timeout error!";

                    } else {
                        errorMessage = "Oops. NoConnectionError!";
                    }

                } else if (error instanceof AuthFailureError) {

                    errorMessage = "Oops. AuthFailureError!";
                } else if (error instanceof ServerError) {

                    errorMessage = "Oops. ServerError!";
                } else if (error instanceof NetworkError) {
                    errorMessage = "Oops. NetworkError!";
                } else if (error instanceof ParseError) {
                    errorMessage = "Oops. ParseError!";
                }
                callback.onErrorResponse(errorMessage);
            }
        });

        // Adding request to request queue
        ApplicationController.getInstance().addToRequestQueue(dayCloseRequest, REQ_TAG);


    }



    /**
     * GetProductTypes from Web Server using Volley
     */
    public static void webGetProductTypes(final webObjectCallback callback) {

        final String REQ_TAG = "volley_get_product_type_tag";

        StringRequest getProductTypeRequest = new StringRequest(Request.Method.GET, ConfigURL.URL_PRODUCT_TYPE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject object = new JSONObject();
                        try {
                            object = new JSONObject(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        callback.onResponse(object);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String errorMessage = null;

                if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                    if (error.getClass().equals(TimeoutError.class)) {
                        // Show timeout error message
                        errorMessage = "Oops. Timeout error!";

                    } else {
                        errorMessage = "Oops. NoConnectionError!";
                    }

                } else if (error instanceof AuthFailureError) {

                    errorMessage = "Oops. AuthFailureError!";
                } else if (error instanceof ServerError) {

                    errorMessage = "Oops. ServerError!";
                } else if (error instanceof NetworkError) {
                    errorMessage = "Oops. NetworkError!";
                } else if (error instanceof ParseError) {
                    errorMessage = "Oops. ParseError!";
                }
                callback.onErrorResponse(errorMessage);

            }
        });

        // Adding request to request queue
        ApplicationController.getInstance().addToRequestQueue(getProductTypeRequest, REQ_TAG);

    }

    /**
     * Get Van Stock by types from Web Server using Volley
     */
    public static void webGetVanStock(final webObjectCallback callback, JSONObject object) {

        final String REQ_TAG = "volley_get_stock_tag";

        JsonObjectRequest getVanStockRequest = new JsonObjectRequest(Request.Method.POST, ConfigURL.URL_VAN_STOCK, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                callback.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                String errorMessage = null;

                if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                    if (error.getClass().equals(TimeoutError.class)) {
                        // Show timeout error message
                        errorMessage = "Oops. Timeout error!";

                    } else {
                        errorMessage = "Oops. NoConnectionError!";
                    }

                } else if (error instanceof AuthFailureError) {

                    errorMessage = "Oops. AuthFailureError!";
                } else if (error instanceof ServerError) {

                    errorMessage = "Oops. ServerError!";
                } else if (error instanceof NetworkError) {
                    errorMessage = "Oops. NetworkError!";
                } else if (error instanceof ParseError) {
                    errorMessage = "Oops. ParseError!";
                }
                callback.onErrorResponse(errorMessage);
            }
        });

        // Adding request to request queue
        ApplicationController.getInstance().addToRequestQueue(getVanStockRequest, REQ_TAG);

    }

    /**
     * Get Van Stock by types from Web Server using Volley
     */
    public static void webGetVanMasterStock(final webObjectCallback callback, JSONObject object) {

        final String REQ_TAG = "volley_get_stock_tag";

        JsonObjectRequest getVanStockRequest = new JsonObjectRequest(Request.Method.POST, ConfigURL.URL_VAN_MASTERSTOCK, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                callback.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                String errorMessage = null;

                if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                    if (error.getClass().equals(TimeoutError.class)) {
                        // Show timeout error message
                        errorMessage = "Oops. Timeout error!";

                    } else {
                        errorMessage = "Oops. NoConnectionError!";
                    }

                } else if (error instanceof AuthFailureError) {

                    errorMessage = "Oops. AuthFailureError!";
                } else if (error instanceof ServerError) {

                    errorMessage = "Oops. ServerError!";
                } else if (error instanceof NetworkError) {
                    errorMessage = "Oops. NetworkError!";
                } else if (error instanceof ParseError) {
                    errorMessage = "Oops. ParseError!";
                }
                callback.onErrorResponse(errorMessage);
            }
        });


        // Adding request to request queue
        ApplicationController.getInstance().addToRequestQueue(getVanStockRequest, REQ_TAG);

    }

    /**
     * Get Bonus Report from Web Server using Volley
     */
    public static void webBonusReport(final webObjectCallback callback, JSONObject object) {

        final String REQ_TAG = "volley_get_bonus_report";

        JsonObjectRequest getVanStockRequest = new JsonObjectRequest(Request.Method.POST, ConfigURL.URL_GET_BONUS_REPORT, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                callback.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                String errorMessage = null;

                if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                    if (error.getClass().equals(TimeoutError.class)) {
                        // Show timeout error message
                        errorMessage = "Oops. Timeout error!";

                    } else {
                        errorMessage = "Oops. NoConnectionError!";
                    }

                } else if (error instanceof AuthFailureError) {

                    errorMessage = "Oops. AuthFailureError!";
                } else if (error instanceof ServerError) {

                    errorMessage = "Oops. ServerError!";
                } else if (error instanceof NetworkError) {
                    errorMessage = "Oops. NetworkError!";
                } else if (error instanceof ParseError) {
                    errorMessage = "Oops. ParseError!";
                }
                callback.onErrorResponse(errorMessage);
            }
        });


        // Adding request to request queue
        ApplicationController.getInstance().addToRequestQueue(getVanStockRequest, REQ_TAG);

    }

    /**
     * Get Outstanding Report from Web Server using Volley
     */
    public static void webOutstandingReport(final webObjectCallback callback, JSONObject object) {

        final String REQ_TAG = "volley_get_outstanding_report";

        JsonObjectRequest getVanStockRequest = new JsonObjectRequest(Request.Method.POST, ConfigURL.URL_GET_OUTSTANDING_REPORT, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                callback.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                String errorMessage = null;

                if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                    if (error.getClass().equals(TimeoutError.class)) {
                        // Show timeout error message
                        errorMessage = "Oops. Timeout error!";

                    } else {
                        errorMessage = "Oops. NoConnectionError!";
                    }

                } else if (error instanceof AuthFailureError) {

                    errorMessage = "Oops. AuthFailureError!";
                } else if (error instanceof ServerError) {

                    errorMessage = "Oops. ServerError!";
                } else if (error instanceof NetworkError) {
                    errorMessage = "Oops. NetworkError!";
                } else if (error instanceof ParseError) {
                    errorMessage = "Oops. ParseError!";
                }
                callback.onErrorResponse(errorMessage);
            }
        });

        // Adding request to request queue
        ApplicationController.getInstance().addToRequestQueue(getVanStockRequest, REQ_TAG);

    }



    /**
     * Get Banks from Web Server using Volley
     */
    public static void webGetBanks(final webObjectCallback callback) {

        final String REQ_TAG = "volley_get_bank_tag";
        //http://13.233.131.201/alhudaibah_test/Settings/api_get_bank_list/

        StringRequest getCustomerBank = new StringRequest(Request.Method.GET, ConfigURL.URL_GET_BANK,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject object = new JSONObject();
                        try {
                            object = new JSONObject(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        callback.onResponse(object);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String errorMessage = null;

                if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                    if (error.getClass().equals(TimeoutError.class)) {
                        // Show timeout error message
                        errorMessage = "Oops. Timeout error!";

                    } else {
                        errorMessage = "Oops. NoConnectionError!";
                    }

                } else if (error instanceof AuthFailureError) {

                    errorMessage = "Oops. AuthFailureError!";
                } else if (error instanceof ServerError) {

                    errorMessage = "Oops. ServerError!";
                } else if (error instanceof NetworkError) {
                    errorMessage = "Oops. NetworkError!";
                } else if (error instanceof ParseError) {
                    errorMessage = "Oops. ParseError!";
                }
                callback.onErrorResponse(errorMessage);

            }
        });


        // Adding request to request queue
        ApplicationController.getInstance().addToRequestQueue(getCustomerBank, REQ_TAG);
    }



    /**
     * Get Expense from Web Server using Volley
     */
    public static void webAllExpense(final webObjectCallback callback) {

        final String REQ_TAG = "volley_get_expense_tag";
        //http://13.233.131.201/alhudaibah_test/Settings/api_get_bank_list/

        StringRequest getCustomerBank = new StringRequest(Request.Method.GET, ConfigURL.URL_GET_ALL_EXPENSE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject object = new JSONObject();
                        try {
                            object = new JSONObject(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        callback.onResponse(object);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String errorMessage = null;

                if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                    if (error.getClass().equals(TimeoutError.class)) {
                        // Show timeout error message
                        errorMessage = "Oops. Timeout error!";

                    } else {
                        errorMessage = "Oops. NoConnectionError!";
                    }

                } else if (error instanceof AuthFailureError) {

                    errorMessage = "Oops. AuthFailureError!";
                } else if (error instanceof ServerError) {

                    errorMessage = "Oops. ServerError!";
                } else if (error instanceof NetworkError) {
                    errorMessage = "Oops. NetworkError!";
                } else if (error instanceof ParseError) {
                    errorMessage = "Oops. ParseError!";
                }
                callback.onErrorResponse(errorMessage);

            }
        });


        // Adding request to request queue
        ApplicationController.getInstance().addToRequestQueue(getCustomerBank, REQ_TAG);
    }


    /**
     * Group By Shops Web Server using Volley
     */
    public static void webGroupByShop(final webObjectCallback callback, JSONObject object) {

        final String REQ_TAG = "volley_group_by_shop_tag";

        JsonObjectRequest groupByShopRequest = new JsonObjectRequest(Request.Method.POST, ConfigURL.URL_GROUPSBYCUSTOMERLIST, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                callback.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                String errorMessage = "Unknown error!";

                if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                    if (error.getClass().equals(TimeoutError.class)) {
                        // Show timeout error message
                        errorMessage = "Oops. Timeout error!";

                    } else {
                        errorMessage = "Oops. NoConnectionError!";
                    }

                } else if (error instanceof AuthFailureError) {

                    errorMessage = "Oops. AuthFailureError!";
                } else if (error instanceof ServerError) {

                    errorMessage = "Oops. ServerError!";
                } else if (error instanceof NetworkError) {
                    errorMessage = "Oops. NetworkError!";
                } else if (error instanceof ParseError) {
                    errorMessage = "Oops. ParseError!";
                }
                callback.onErrorResponse(errorMessage);
            }
        });

        // Adding request to request queue
        ApplicationController.getInstance().addToRequestQueue(groupByShopRequest, REQ_TAG);


    }


    /**
     * Registered Shops Web Server using Volley
     */
    public static void webRegisteredShop(final webObjectCallback callback, JSONObject object) {

        final String REQ_TAG = "volley_registered_shop_tag";

        JsonObjectRequest registeredShopRequest = new JsonObjectRequest(Request.Method.POST, ConfigURL.URL_REGISTERED_SHOP, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                callback.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                String errorMessage = "Unknown error!";

                if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                    if (error.getClass().equals(TimeoutError.class)) {
                        // Show timeout error message
                        errorMessage = "Oops. Timeout error!";

                    } else {
                        errorMessage = "Oops. NoConnectionError!";
                    }

                } else if (error instanceof AuthFailureError) {

                    errorMessage = "Oops. AuthFailureError!";
                } else if (error instanceof ServerError) {

                    errorMessage = "Oops. ServerError!";
                } else if (error instanceof NetworkError) {
                    errorMessage = "Oops. NetworkError!";
                } else if (error instanceof ParseError) {
                    errorMessage = "Oops. ParseError!";
                }
                callback.onErrorResponse(errorMessage);
            }
        });

        // Adding request to request queue
        ApplicationController.getInstance().addToRequestQueue(registeredShopRequest, REQ_TAG);
    }

    /**
     * All route Shops Web Server using Volley
     */
    public static void webAllRouteShop(final webObjectCallback callback, JSONObject object) {

        final String REQ_TAG = "volley_route_shop_tag";

        JsonObjectRequest routeShopRequest = new JsonObjectRequest(Request.Method.POST, ConfigURL.URL_ALLROUTE_SHOP, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                callback.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                String errorMessage = "Unknown error!";

                if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                    if (error.getClass().equals(TimeoutError.class)) {
                        // Show timeout error message
                        errorMessage = "Oops. Timeout error!";

                    } else {
                        errorMessage = "Oops. NoConnectionError!";
                    }

                } else if (error instanceof AuthFailureError) {

                    errorMessage = "Oops. AuthFailureError!";
                } else if (error instanceof ServerError) {

                    errorMessage = "Oops. ServerError!";
                } else if (error instanceof NetworkError) {
                    errorMessage = "Oops. NetworkError!";
                } else if (error instanceof ParseError) {
                    errorMessage = "Oops. ParseError!";
                }
                callback.onErrorResponse(errorMessage);
            }
        });

        // Adding request to request queue
        ApplicationController.getInstance().addToRequestQueue(routeShopRequest, REQ_TAG);

    }

    /**
     * GetInvoice Details Web Server using Volley
     */
    public static void webGetReturnInvoiceDetails(final webObjectCallback callback, JSONObject object) {

        final String REQ_TAG = "volley_return_invoice_details_tag";

        JsonObjectRequest invoiceDetailsRequest = new JsonObjectRequest(Request.Method.POST, ConfigURL.URL_INVOICE_DETAILS, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                callback.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                String errorMessage = null;

                if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                    if (error.getClass().equals(TimeoutError.class)) {
                        // Show timeout error message
                        errorMessage = "Oops. Timeout error!";

                    } else {
                        errorMessage = "Oops. NoConnectionError!";
                    }

                } else if (error instanceof AuthFailureError) {

                    errorMessage = "Oops. AuthFailureError!";
                } else if (error instanceof ServerError) {

                    errorMessage = "Oops. ServerError!";
                } else if (error instanceof NetworkError) {
                    errorMessage = "Oops. NetworkError!";
                } else if (error instanceof ParseError) {
                    errorMessage = "Oops. ParseError!";
                }
                callback.onErrorResponse(errorMessage);
            }
        });

        // Adding request to request queue
        ApplicationController.getInstance().addToRequestQueue(invoiceDetailsRequest, REQ_TAG);


    }

    /**
     * Get Receipt Web Server using Volley
     */
    public static void webGetReceipt_(final webObjectCallback callback, JSONObject object) {

        final String REQ_TAG = "volley_getReceipt_tag";

        JsonObjectRequest receiptRequest = new JsonObjectRequest(Request.Method.POST, ConfigURL.URL_GET_PENDING_RECEIPT, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                callback.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                String errorMessage = null;

                if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                    if (error.getClass().equals(TimeoutError.class)) {
                        // Show timeout error message
                        errorMessage = "Oops. Timeout error!";

                    } else {
                        errorMessage = "Oops. NoConnectionError!";
                    }

                } else if (error instanceof AuthFailureError) {

                    errorMessage = "Oops. AuthFailureError!";
                } else if (error instanceof ServerError) {

                    errorMessage = "Oops. ServerError!";
                } else if (error instanceof NetworkError) {
                    errorMessage = "Oops. NetworkError!";
                } else if (error instanceof ParseError) {
                    errorMessage = "Oops. ParseError!";
                }
                callback.onErrorResponse(errorMessage);
            }
        });

        // Adding request to request queue
        ApplicationController.getInstance().addToRequestQueue(receiptRequest, REQ_TAG);


    }






    /**
     * Get All Receipt Web Server using Volley
     */
    public static void webGetAllReceipt(final webObjectCallback callback, JSONObject object) {

        final String REQ_TAG = "volley_get_all_Receipt_tag";

        JsonObjectRequest allReceiptRequest = new JsonObjectRequest(Request.Method.POST, URL_GET_ALL_RECEIPT, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                callback.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                String errorMessage = null;

                if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                    if (error.getClass().equals(TimeoutError.class)) {
                        // Show timeout error message
                        errorMessage = "Oops. Timeout error!";

                    } else {
                        errorMessage = "Oops. NoConnectionError!";
                    }

                } else if (error instanceof AuthFailureError) {

                    errorMessage = "Oops. AuthFailureError!";
                } else if (error instanceof ServerError) {

                    errorMessage = "Oops. ServerError!";
                } else if (error instanceof NetworkError) {
                    errorMessage = "Oops. NetworkError!";
                } else if (error instanceof ParseError) {
                    errorMessage = "Oops. ParseError!";
                }
                callback.onErrorResponse(errorMessage);
            }
        });

        // Adding request to request queue
        ApplicationController.getInstance().addToRequestQueue(allReceiptRequest, REQ_TAG);

    }



    /**
     * Paid Receipt Web Server using Volley
     */
    public static void webPaidReceipt(final webObjectCallback callback, JSONObject object) {

        final String REQ_TAG = "volley_paid_receipt_tag";

        JsonObjectRequest paidReceiptRequest = new JsonObjectRequest(Request.Method.POST, ConfigURL.URL_PAID_RECEIPT, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                callback.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                String errorMessage = null;

                if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                    if (error.getClass().equals(TimeoutError.class)) {
                        // Show timeout error message
                        errorMessage = "Oops. Timeout error!";

                    } else {
                        errorMessage = "Oops. NoConnectionError!";
                    }

                } else if (error instanceof AuthFailureError) {

                    errorMessage = "Oops. AuthFailureError!";
                } else if (error instanceof ServerError) {

                    errorMessage = "Oops. ServerError!";
                } else if (error instanceof NetworkError) {
                    errorMessage = "Oops. NetworkError!";
                } else if (error instanceof ParseError) {
                    errorMessage = "Oops. ParseError!";
                }
                callback.onErrorResponse(errorMessage);
            }
        });

        // Adding request to request queue
        ApplicationController.getInstance().addToRequestQueue(paidReceiptRequest, REQ_TAG);

    }


    /**
     * Cheque Receipt Web Server using Volley
     */
    public static void webPaidChequeReceipt(final webObjectCallback callback, JSONObject object) {

        final String REQ_TAG = "volley_paid_receipt_tag";

        JsonObjectRequest paidReceiptRequest = new JsonObjectRequest(Request.Method.POST, ConfigURL.URL_PAID_CHEQUE_RECEIPT, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                callback.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                String errorMessage = null;

                if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                    if (error.getClass().equals(TimeoutError.class)) {
                        // Show timeout error message
                        errorMessage = "Oops. Timeout error!";

                    } else {
                        errorMessage = "Oops. NoConnectionError!";
                    }

                } else if (error instanceof AuthFailureError) {

                    errorMessage = "Oops. AuthFailureError!";
                } else if (error instanceof ServerError) {

                    errorMessage = "Oops. ServerError!";
                } else if (error instanceof NetworkError) {
                    errorMessage = "Oops. NetworkError!";
                } else if (error instanceof ParseError) {
                    errorMessage = "Oops. ParseError!";
                }
                callback.onErrorResponse(errorMessage);
            }
        });

        // Adding request to request queue
        ApplicationController.getInstance().addToRequestQueue(paidReceiptRequest, REQ_TAG);

    }


    /**
     * Expense Entry Web Server using Volley
     */
    public static void webExpenseEntry(final webObjectCallback callback, JSONObject object) {

        final String REQ_TAG = "volley_paid_receipt_tag";

        JsonObjectRequest paidReceiptRequest = new JsonObjectRequest(Request.Method.POST, ConfigURL.URL_EXPENSE_ENTRY, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                callback.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                String errorMessage = null;

                if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                    if (error.getClass().equals(TimeoutError.class)) {
                        // Show timeout error message
                        errorMessage = "Oops. Timeout error!";

                    } else {
                        errorMessage = "Oops. NoConnectionError!";
                    }

                } else if (error instanceof AuthFailureError) {

                    errorMessage = "Oops. AuthFailureError!";
                } else if (error instanceof ServerError) {

                    errorMessage = "Oops. ServerError!";
                } else if (error instanceof NetworkError) {
                    errorMessage = "Oops. NetworkError!";
                } else if (error instanceof ParseError) {
                    errorMessage = "Oops. ParseError!";
                }
                callback.onErrorResponse(errorMessage);
            }
        });

        // Adding request to request queue
        ApplicationController.getInstance().addToRequestQueue(paidReceiptRequest, REQ_TAG);

    }


    /**
     * Cheque Receipt Web Server using Volley
     */
    public static void webOfflineReceipt(final webObjectCallback callback, JSONObject object) {

        final String REQ_TAG = "volley_paid_receipt_tag";

        JsonObjectRequest paidReceiptRequest = new JsonObjectRequest(Request.Method.POST, ConfigURL.URL_PAID_CHEQUE_RECEIPT, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                callback.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                String errorMessage = null;

                if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                    if (error.getClass().equals(TimeoutError.class)) {
                        // Show timeout error message
                        errorMessage = "Oops. Timeout error!";

                    } else {
                        errorMessage = "Oops. NoConnectionError!";
                    }

                } else if (error instanceof AuthFailureError) {

                    errorMessage = "Oops. AuthFailureError!";
                } else if (error instanceof ServerError) {

                    errorMessage = "Oops. ServerError!";
                } else if (error instanceof NetworkError) {
                    errorMessage = "Oops. NetworkError!";
                } else if (error instanceof ParseError) {
                    errorMessage = "Oops. ParseError!";
                }
                callback.onErrorResponse(errorMessage);
            }
        });

        // Adding request to request queue
        ApplicationController.getInstance().addToRequestQueue(paidReceiptRequest, REQ_TAG);

    }


    /**
     * Cheque Receipt Web Server using Volley
     */
    public static void webOfflineReturn(final webObjectCallback callback, JSONObject object) {

        final String REQ_TAG = "volley_paid_receipt_tag";

        JsonObjectRequest paidReceiptRequest = new JsonObjectRequest(Request.Method.POST, ConfigURL.URL_OFFLINE_RETURN, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                callback.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                String errorMessage = null;

                if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                    if (error.getClass().equals(TimeoutError.class)) {
                        // Show timeout error message
                        errorMessage = "Oops. Timeout error!";

                    } else {
                        errorMessage = "Oops. NoConnectionError!";
                    }

                } else if (error instanceof AuthFailureError) {

                    errorMessage = "Oops. AuthFailureError!";
                } else if (error instanceof ServerError) {

                    errorMessage = "Oops. ServerError!";
                } else if (error instanceof NetworkError) {
                    errorMessage = "Oops. NetworkError!";
                } else if (error instanceof ParseError) {
                    errorMessage = "Oops. ParseError!";
                }
                callback.onErrorResponse(errorMessage);
            }
        });

        // Adding request to request queue
        ApplicationController.getInstance().addToRequestQueue(paidReceiptRequest, REQ_TAG);

    }

    /**
     * sales return Invoice Web Server using Volley
     */
    public static void webSaleReturn(final webObjectCallback callback, JSONObject object) {

        final String REQ_TAG = "volley_sale_return_tag";

        JsonObjectRequest salesReturnRequest = new JsonObjectRequest(Request.Method.POST, URL_SALE_RETURN, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                callback.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                String errorMessage = null;

                if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                    if (error.getClass().equals(TimeoutError.class)) {
                        // Show timeout error message
                        errorMessage = "Oops. Timeout error!";

                    } else {
                        errorMessage = "Oops. NoConnectionError!";
                    }

                } else if (error instanceof AuthFailureError) {

                    errorMessage = "Oops. AuthFailureError!";
                } else if (error instanceof ServerError) {

                    errorMessage = "Oops. ServerError!";
                } else if (error instanceof NetworkError) {
                    errorMessage = "Oops. NetworkError!";
                } else if (error instanceof ParseError) {
                    errorMessage = "Oops. ParseError!";
                }
                callback.onErrorResponse(errorMessage);
            }
        });

        // Adding request to request queue
        ApplicationController.getInstance().addToRequestQueue(salesReturnRequest, REQ_TAG);


    }



    /**
     * Order Place Web Server using Volley
     */
    public static void webPlaceOrder(final webObjectCallback callback, JSONObject object) {

        final String REQ_TAG = "volley_place_order_tag";

        JsonObjectRequest placeOrderRequest = new JsonObjectRequest(Request.Method.POST, ConfigURL.URL_PLACE_ORDER, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                callback.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                String errorMessage = null;

                if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                    if (error.getClass().equals(TimeoutError.class)) {
                        // Show timeout error message
                        errorMessage = "Oops. Timeout error!";

                    } else {
                        errorMessage = "Oops. NoConnectionError!";
                    }

                } else if (error instanceof AuthFailureError) {

                    errorMessage = "Oops. AuthFailureError!";
                } else if (error instanceof ServerError) {

                    errorMessage = "Oops. ServerError!";
                } else if (error instanceof NetworkError) {
                    errorMessage = "Oops. NetworkError!";
                } else if (error instanceof ParseError) {
                    errorMessage = "Oops. ParseError!";
                }
                callback.onErrorResponse(errorMessage);
            }
        });

        // Adding request to request queue
        ApplicationController.getInstance().addToRequestQueue(placeOrderRequest, REQ_TAG);

    }


    /**
     * Order Quotation Web Server using Volley
     */
    public static void webPlaceQuotation(final webObjectCallback callback, JSONObject object) {

        final String REQ_TAG = "volley_quotation_order_tag";

        JsonObjectRequest quotationOrderRequest = new JsonObjectRequest(Request.Method.POST, ConfigURL.URL_QUOTATION_ORDER, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                callback.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                String errorMessage = null;

                if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                    if (error.getClass().equals(TimeoutError.class)) {
                        // Show timeout error message
                        errorMessage = "Oops. Timeout error!";

                    } else {
                        errorMessage = "Oops. NoConnectionError!";
                    }

                } else if (error instanceof AuthFailureError) {

                    errorMessage = "Oops. AuthFailureError!";
                } else if (error instanceof ServerError) {

                    errorMessage = "Oops. ServerError!";
                } else if (error instanceof NetworkError) {
                    errorMessage = "Oops. NetworkError!";
                } else if (error instanceof ParseError) {
                    errorMessage = "Oops. ParseError!";
                }
                callback.onErrorResponse(errorMessage);
            }
        });

        // Adding request to request queue
        ApplicationController.getInstance().addToRequestQueue(quotationOrderRequest, REQ_TAG);

    }
//haris added on 07-12-2020
    /**
     * Stock Transfer
     */
    public static void webVanToWarehouseTransferApprove(final webObjectCallback callback, JSONObject object) {

        final String REQ_TAG = "volley_return_tag";

        JsonObjectRequest placeOrderRequest = new JsonObjectRequest(Request.Method.POST, URL_VAN_TO_WAREHOUSE_APPROVE, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                callback.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                String errorMessage = null;

                if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                    if (error.getClass().equals(TimeoutError.class)) {
                        // Show timeout error message
                        errorMessage = "Oops. Timeout error!";

                    } else {
                        errorMessage = "Oops. NoConnectionError!";
                    }

                } else if (error instanceof AuthFailureError) {

                    errorMessage = "Oops. AuthFailureError!";
                } else if (error instanceof ServerError) {

                    errorMessage = "Oops. ServerError!";
                } else if (error instanceof NetworkError) {
                    errorMessage = "Oops. NetworkError!";
                } else if (error instanceof ParseError) {
                    errorMessage = "Oops. ParseError!";
                }
                callback.onErrorResponse(errorMessage);
            }
        });

        // Adding request to request queue
        ApplicationController.getInstance().addToRequestQueue(placeOrderRequest, REQ_TAG);
    }



    /**
     * Return Web Server using Volley
     */
    public static void webReturn(final webObjectCallback callback, JSONObject object) {

        final String REQ_TAG = "volley_return_tag";

        JsonObjectRequest placeOrderRequest = new JsonObjectRequest(Request.Method.POST, URL_MULTIPLE_RETURN, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                callback.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                String errorMessage = null;

                if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                    if (error.getClass().equals(TimeoutError.class)) {
                        // Show timeout error message
                        errorMessage = "Oops. Timeout error!";

                    } else {
                        errorMessage = "Oops. NoConnectionError!";
                    }

                } else if (error instanceof AuthFailureError) {

                    errorMessage = "Oops. AuthFailureError!";
                } else if (error instanceof ServerError) {

                    errorMessage = "Oops. ServerError!";
                } else if (error instanceof NetworkError) {
                    errorMessage = "Oops. NetworkError!";
                } else if (error instanceof ParseError) {
                    errorMessage = "Oops. ParseError!";
                }
                callback.onErrorResponse(errorMessage);
            }
        });

        // Adding request to request queue
        ApplicationController.getInstance().addToRequestQueue(placeOrderRequest, REQ_TAG);


    }


    /**
     * Order Place Web Server using Volley
     */
    public static void webNoSale(final webObjectCallback callback, JSONObject object) {

        final String REQ_TAG = "volley_no_sale_tag";

        JsonObjectRequest noSaleRequest = new JsonObjectRequest(Request.Method.POST, URL_NO_SALE, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                callback.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                String errorMessage = null;

                if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                    if (error.getClass().equals(TimeoutError.class)) {
                        // Show timeout error message
                        errorMessage = "Oops. Timeout error!";

                    } else {
                        errorMessage = "Oops. NoConnectionError!";
                    }

                } else if (error instanceof AuthFailureError) {

                    errorMessage = "Oops. AuthFailureError!";
                } else if (error instanceof ServerError) {

                    errorMessage = "Oops. ServerError!";
                } else if (error instanceof NetworkError) {
                    errorMessage = "Oops. NetworkError!";
                } else if (error instanceof ParseError) {
                    errorMessage = "Oops. ParseError!";
                }
                callback.onErrorResponse(errorMessage);
            }
        });

        // Adding request to request queue
        ApplicationController.getInstance().addToRequestQueue(noSaleRequest, REQ_TAG);


    }



    /**
     * Add Customer Web Server using Volley
     */
    public static void webAddCustomer(final webObjectCallback callback, JSONObject object) {

        final String REQ_TAG = "volley_addCustomer_tag";

        Log.e("url",ConfigURL.URL_ADD_CUSTOMER);
        JsonObjectRequest addCustomerRequest = new JsonObjectRequest(Request.Method.POST, ConfigURL.URL_ADD_CUSTOMER, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                callback.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                String errorMessage = null;

                if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                    if (error.getClass().equals(TimeoutError.class)) {
                        // Show timeout error message
                        errorMessage = "Oops. Timeout error!";

                    } else {
                        errorMessage = "Oops. NoConnectionError!";
                    }

                } else if (error instanceof AuthFailureError) {

                    errorMessage = "Oops. AuthFailureError!";
                } else if (error instanceof ServerError) {

                    errorMessage = "Oops. ServerError!";
                } else if (error instanceof NetworkError) {
                    errorMessage = "Oops. NetworkError!";
                } else if (error instanceof ParseError) {
                    errorMessage = "Oops. ParseError!";
                }
                callback.onErrorResponse(errorMessage);
            }
        });

        // Adding request to request queue
        ApplicationController.getInstance().addToRequestQueue(addCustomerRequest, REQ_TAG);


    }




    /**
     * Add Customer Web Server using Volley
     */

    /**
     * GetProductTypes from Web Server using Volley
     */
    public static void webGetCustomerDivisionanType(final webObjectCallback callback) {

        final String REQ_TAG = "volley_customer_divi_tag";

        StringRequest getCustomerDivRequest = new StringRequest(Request.Method.GET, ConfigURL.URL_GET_CUSTOMER_TYPE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject object = new JSONObject();
                        try {
                            object = new JSONObject(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        callback.onResponse(object);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String errorMessage = null;

                if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                    if (error.getClass().equals(TimeoutError.class)) {
                        // Show timeout error message
                        errorMessage = "Oops. Timeout error!";

                    } else {
                        errorMessage = "Oops. NoConnectionError!";
                    }

                } else if (error instanceof AuthFailureError) {

                    errorMessage = "Oops. AuthFailureError!";
                } else if (error instanceof ServerError) {

                    errorMessage = "Oops. ServerError!";
                } else if (error instanceof NetworkError) {
                    errorMessage = "Oops. NetworkError!";
                } else if (error instanceof ParseError) {
                    errorMessage = "Oops. ParseError!";
                }
                callback.onErrorResponse(errorMessage);

            }
        });


        // Adding request to request queue
        ApplicationController.getInstance().addToRequestQueue(getCustomerDivRequest, REQ_TAG);


    }




    /**
     * change password Web Server using Volley
     */
    public static void webChangePassword(final webObjectCallback callback, JSONObject object) {

        final String REQ_TAG = "volley_change_password_tag";

        JsonObjectRequest changePasswordRequest = new JsonObjectRequest(Request.Method.POST, ConfigURL.URL_CHANGE_PASSWORD, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                callback.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                String errorMessage = null;

                if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                    if (error.getClass().equals(TimeoutError.class)) {
                        // Show timeout error message
                        errorMessage = "Oops. Timeout error!";

                    } else {
                        errorMessage = "Oops. NoConnectionError!";
                    }

                } else if (error instanceof AuthFailureError) {

                    errorMessage = "Oops. AuthFailureError!";
                } else if (error instanceof ServerError) {

                    errorMessage = "Oops. ServerError!";
                } else if (error instanceof NetworkError) {
                    errorMessage = "Oops. NetworkError!";
                } else if (error instanceof ParseError) {
                    errorMessage = "Oops. ParseError!";
                }
                callback.onErrorResponse(errorMessage);
            }
        });

        // Adding request to request queue
        ApplicationController.getInstance().addToRequestQueue(changePasswordRequest, REQ_TAG);


    }





    /**
     *Update customer Server using Volley
     */
    public static void webUpdateCustomer(final webObjectCallback callback, JSONObject object) {

        final String REQ_TAG = "volley_update_customer_tag";

        JsonObjectRequest updateCustomerRequest = new JsonObjectRequest(Request.Method.POST, URL_UPDATE_CUSTOMER, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                callback.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                String errorMessage = null;

                if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                    if (error.getClass().equals(TimeoutError.class)) {
                        // Show timeout error message
                        errorMessage = "Oops. Timeout error!";

                    } else {
                        errorMessage = "Oops. NoConnectionError!";
                    }

                } else if (error instanceof AuthFailureError) {

                    errorMessage = "Oops. AuthFailureError!";
                } else if (error instanceof ServerError) {

                    errorMessage = "Oops. ServerError!";
                } else if (error instanceof NetworkError) {
                    errorMessage = "Oops. NetworkError!";
                } else if (error instanceof ParseError) {
                    errorMessage = "Oops. ParseError!";
                }
                callback.onErrorResponse(errorMessage);
            }
        });

        // Adding request to request queue
        ApplicationController.getInstance().addToRequestQueue(updateCustomerRequest, REQ_TAG);

    }


    /**
     * Return Web Server using Volley
     */
    public static void webContraVoucher(final webObjectCallback callback, JSONObject object) {

        final String REQ_TAG = "volley_return_tag";

        JsonObjectRequest placeOrderRequest = new JsonObjectRequest(Request.Method.POST, URL_CONTRA_VOUCHER, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                callback.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                String errorMessage = null;

                if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                    if (error.getClass().equals(TimeoutError.class)) {
                        // Show timeout error message
                        errorMessage = "Oops. Timeout error!";

                    } else {
                        errorMessage = "Oops. NoConnectionError!";
                    }

                } else if (error instanceof AuthFailureError) {

                    errorMessage = "Oops. AuthFailureError!";
                } else if (error instanceof ServerError) {

                    errorMessage = "Oops. ServerError!";
                } else if (error instanceof NetworkError) {
                    errorMessage = "Oops. NetworkError!";
                } else if (error instanceof ParseError) {
                    errorMessage = "Oops. ParseError!";
                }
                callback.onErrorResponse(errorMessage);
            }
        });

        // Adding request to request queue
        ApplicationController.getInstance().addToRequestQueue(placeOrderRequest, REQ_TAG);

    }

    /**
     * Cash in Hand Web service using Volley
     */
    public static void webGetCashInHand(final webObjectCallback callback, JSONObject object) {

        final String REQ_TAG = "volley_return_tag";

        JsonObjectRequest placeOrderRequest = new JsonObjectRequest(Request.Method.POST, URL_GET_CASHIN_HAND, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                callback.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                String errorMessage = null;

                if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                    if (error.getClass().equals(TimeoutError.class)) {
                        // Show timeout error message
                        errorMessage = "Oops. Timeout error!";

                    } else {
                        errorMessage = "Oops. NoConnectionError!";
                    }

                } else if (error instanceof AuthFailureError) {

                    errorMessage = "Oops. AuthFailureError!";
                } else if (error instanceof ServerError) {

                    errorMessage = "Oops. ServerError!";
                } else if (error instanceof NetworkError) {
                    errorMessage = "Oops. NetworkError!";
                } else if (error instanceof ParseError) {
                    errorMessage = "Oops. ParseError!";
                }
                callback.onErrorResponse(errorMessage);
            }
        });

        // Adding request to request queue
        ApplicationController.getInstance().addToRequestQueue(placeOrderRequest, REQ_TAG);


    }


    /**
     * Get Parameters Web Server using Volley
     */
    public static void webGetSettingsParameter(final webObjectCallback callback) {

        final String REQ_TAG = "volley_get_settings_tag";

        StringRequest getCustomerBank = new StringRequest(Request.Method.GET, ConfigURL.URL_SETTINGS_PARAMETERS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject object = new JSONObject();
                        try {
                            object = new JSONObject(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        callback.onResponse(object);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String errorMessage = null;

                if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                    if (error.getClass().equals(TimeoutError.class)) {
                        // Show timeout error message
                        errorMessage = "Oops. Timeout error!";

                    } else {
                        errorMessage = "Oops. NoConnectionError!";
                    }

                } else if (error instanceof AuthFailureError) {

                    errorMessage = "Oops. AuthFailureError!";
                } else if (error instanceof ServerError) {

                    errorMessage = "Oops. ServerError!";
                } else if (error instanceof NetworkError) {
                    errorMessage = "Oops. NetworkError!";
                } else if (error instanceof ParseError) {
                    errorMessage = "Oops. ParseError!";
                }
                callback.onErrorResponse(errorMessage);

            }
        });
        // Adding request to request queue
        ApplicationController.getInstance().addToRequestQueue(getCustomerBank, REQ_TAG);
    }



    public interface webObjectCallback {
        void onResponse(JSONObject response);

        void onErrorResponse(String error);
    }
    /**
     * Get Bonus Report from Web Server using Volley
     */
    public static void GetExpenseDetails(final webObjectCallback callback, JSONObject object) {

        final String REQ_TAG = "volley_get_bonus_report";

        JsonObjectRequest getVanStockRequest = new JsonObjectRequest(Request.Method.POST, ConfigURL.URL_GET_EXPENSE_DETAILS, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                callback.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                String errorMessage = null;

                if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                    if (error.getClass().equals(TimeoutError.class)) {
                        // Show timeout error message
                        errorMessage = "Oops. Timeout error!";

                    } else {
                        errorMessage = "Oops. NoConnectionError!";
                    }

                } else if (error instanceof AuthFailureError) {

                    errorMessage = "Oops. AuthFailureError!";
                } else if (error instanceof ServerError) {

                    errorMessage = "Oops. ServerError!";
                } else if (error instanceof NetworkError) {
                    errorMessage = "Oops. NetworkError!";
                } else if (error instanceof ParseError) {
                    errorMessage = "Oops. ParseError!";
                }
                callback.onErrorResponse(errorMessage);
            }
        });


        // Adding request to request queue
        ApplicationController.getInstance().addToRequestQueue(getVanStockRequest, REQ_TAG);

    }

    /**
     * Edit Customer Web Server using Volley
     */
    public static void webEditCustomer(final webObjectCallback callback, JSONObject object) {

        final String REQ_TAG = "volley_addCustomer_tag";

        JsonObjectRequest addCustomerRequest = new JsonObjectRequest(Request.Method.POST, ConfigURL.URL_EDIT_CUSTOMER, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                callback.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                String errorMessage = null;

                if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                    if (error.getClass().equals(TimeoutError.class)) {
                        // Show timeout error message
                        errorMessage = "Oops. Timeout error!";

                    } else {
                        errorMessage = "Oops. NoConnectionError!";
                    }

                } else if (error instanceof AuthFailureError) {

                    errorMessage = "Oops. AuthFailureError!";
                } else if (error instanceof ServerError) {

                    errorMessage = "Oops. ServerError!";
                } else if (error instanceof NetworkError) {
                    errorMessage = "Oops. NetworkError!";
                } else if (error instanceof ParseError) {
                    errorMessage = "Oops. ParseError!";
                }
                callback.onErrorResponse(errorMessage);
            }
        });

        // Adding request to request queue
        ApplicationController.getInstance().addToRequestQueue(addCustomerRequest, REQ_TAG);


    }


    /**
     * Edit Customer Web Server using Volley
     */
    public static void webGetCustomerDetailsbyCustId(final webObjectCallback callback, JSONObject object) {

        final String REQ_TAG = "volley_addCustomer_tag";

        JsonObjectRequest addCustomerRequest = new JsonObjectRequest(Request.Method.POST, ConfigURL.URL_GET_CUSTOMER_BYCUSTID, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                callback.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                String errorMessage = null;

                if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                    if (error.getClass().equals(TimeoutError.class)) {
                        // Show timeout error message
                        errorMessage = "Oops. Timeout error!";

                    } else {
                        errorMessage = "Oops. NoConnectionError!";
                    }

                } else if (error instanceof AuthFailureError) {

                    errorMessage = "Oops. AuthFailureError!";
                } else if (error instanceof ServerError) {

                    errorMessage = "Oops. ServerError!";
                } else if (error instanceof NetworkError) {
                    errorMessage = "Oops. NetworkError!";
                } else if (error instanceof ParseError) {
                    errorMessage = "Oops. ParseError!";
                }
                callback.onErrorResponse(errorMessage);
            }
        });

        // Adding request to request queue
        ApplicationController.getInstance().addToRequestQueue(addCustomerRequest, REQ_TAG);


    }
    /**
     * Stock Transfer
     */
    public static void webGetStocktransferDetails(final webObjectCallback callback, JSONObject object) {

        final String REQ_TAG = "volley_return_tag";

        JsonObjectRequest placeOrderRequest = new JsonObjectRequest(Request.Method.POST, URL_STOCK_TRANSFER_DETAILS, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                callback.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                String errorMessage = null;

                if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                    if (error.getClass().equals(TimeoutError.class)) {
                        // Show timeout error message
                        errorMessage = "Oops. Timeout error!";

                    } else {
                        errorMessage = "Oops. NoConnectionError!";
                    }

                } else if (error instanceof AuthFailureError) {

                    errorMessage = "Oops. AuthFailureError!";
                } else if (error instanceof ServerError) {

                    errorMessage = "Oops. ServerError!";
                } else if (error instanceof NetworkError) {
                    errorMessage = "Oops. NetworkError!";
                } else if (error instanceof ParseError) {
                    errorMessage = "Oops. ParseError!";
                }
                callback.onErrorResponse(errorMessage);
            }
        });

        // Adding request to request queue
        ApplicationController.getInstance().addToRequestQueue(placeOrderRequest, REQ_TAG);


    }

    /**
     * Stock Transfer
     */
    public static void webGetStocktransferApprove(final webObjectCallback callback, JSONObject object) {

        final String REQ_TAG = "volley_return_tag";

        JsonObjectRequest placeOrderRequest = new JsonObjectRequest(Request.Method.POST, URL_STOCK_TRANSFER_APPROVE, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                callback.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                String errorMessage = null;

                if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                    if (error.getClass().equals(TimeoutError.class)) {
                        // Show timeout error message
                        errorMessage = "Oops. Timeout error!";

                    } else {
                        errorMessage = "Oops. NoConnectionError!";
                    }

                } else if (error instanceof AuthFailureError) {

                    errorMessage = "Oops. AuthFailureError!";
                } else if (error instanceof ServerError) {

                    errorMessage = "Oops. ServerError!";
                } else if (error instanceof NetworkError) {
                    errorMessage = "Oops. NetworkError!";
                } else if (error instanceof ParseError) {
                    errorMessage = "Oops. ParseError!";
                }
                callback.onErrorResponse(errorMessage);
            }
        });

        // Adding request to request queue
        ApplicationController.getInstance().addToRequestQueue(placeOrderRequest, REQ_TAG);
    }
    /**
     * Stock Transfer
     */
    public static void webGetStocktransfer(final webObjectCallback callback, JSONObject object) {

        final String REQ_TAG = "volley_return_tag";

        JsonObjectRequest placeOrderRequest = new JsonObjectRequest(Request.Method.POST, URL_STOCK_TRANSFER, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                callback.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                String errorMessage = null;

                if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                    if (error.getClass().equals(TimeoutError.class)) {
                        // Show timeout error message
                        errorMessage = "Oops. Timeout error!";

                    } else {
                        errorMessage = "Oops. NoConnectionError!";
                    }

                } else if (error instanceof AuthFailureError) {

                    errorMessage = "Oops. AuthFailureError!";
                } else if (error instanceof ServerError) {

                    errorMessage = "Oops. ServerError!";
                } else if (error instanceof NetworkError) {
                    errorMessage = "Oops. NetworkError!";
                } else if (error instanceof ParseError) {
                    errorMessage = "Oops. ParseError!";
                }
                callback.onErrorResponse(errorMessage);
            }
        });

        // Adding request to request queue
        ApplicationController.getInstance().addToRequestQueue(placeOrderRequest, REQ_TAG);


    }
    /**
     * Get Banks from Web Server using Volley
     */
    public static void webGetVehicledetails(final webObjectCallback callback) {

        final String REQ_TAG = "volley_get_vehicle_tag";


        StringRequest getVehicledetails = new StringRequest(Request.Method.GET, ConfigURL.URL_GET_VEHICLE_LIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject object = new JSONObject();
                        try {
                            object = new JSONObject(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        callback.onResponse(object);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String errorMessage = null;

                if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                    if (error.getClass().equals(TimeoutError.class)) {
                        // Show timeout error message
                        errorMessage = "Oops. Timeout error!";

                    } else {
                        errorMessage = "Oops. NoConnectionError!";
                    }

                } else if (error instanceof AuthFailureError) {

                    errorMessage = "Oops. AuthFailureError!";
                } else if (error instanceof ServerError) {

                    errorMessage = "Oops. ServerError!";
                } else if (error instanceof NetworkError) {
                    errorMessage = "Oops. NetworkError!";
                } else if (error instanceof ParseError) {
                    errorMessage = "Oops. ParseError!";
                }
                callback.onErrorResponse(errorMessage);

            }
        });


        // Adding request to request queue
        ApplicationController.getInstance().addToRequestQueue(getVehicledetails, REQ_TAG);
    }

    /**
     * Get All Receipt Web Server using Volley
     */
    public static void webGetAllPendingInvoices(final webObjectCallback callback, JSONObject object) {

        final String REQ_TAG = "volley_get_all_Pendinginvoices_tag";

        JsonObjectRequest allReceiptRequest = new JsonObjectRequest(Request.Method.POST, URL_GET_ALL_PENDING_INVOICES, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                callback.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                String errorMessage = null;

                if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                    if (error.getClass().equals(TimeoutError.class)) {
                        // Show timeout error message
                        errorMessage = "Oops. Timeout error!";

                    } else {
                        errorMessage = "Oops. NoConnectionError!";
                    }

                } else if (error instanceof AuthFailureError) {

                    errorMessage = "Oops. AuthFailureError!";
                } else if (error instanceof ServerError) {

                    errorMessage = "Oops. ServerError!";
                } else if (error instanceof NetworkError) {
                    errorMessage = "Oops. NetworkError!";
                } else if (error instanceof ParseError) {
                    errorMessage = "Oops. ParseError!";
                }
                callback.onErrorResponse(errorMessage);
            }
        });

        // Adding request to request queue
        ApplicationController.getInstance().addToRequestQueue(allReceiptRequest, REQ_TAG);

    }

    /**
     * Billwise Receipt Web Server using Volley
     */
    public static void webBillwiseReceipt(final webObjectCallback callback, JSONObject object) {

        final String REQ_TAG = "volley_billwise_receipt_tag";

        JsonObjectRequest billwiseReceiptRequest = new JsonObjectRequest(Request.Method.POST, ConfigURL.URL_BILLWISE_RECEIPT, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                callback.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                String errorMessage = null;

                if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                    if (error.getClass().equals(TimeoutError.class)) {
                        // Show timeout error message
                        errorMessage = "Oops. Timeout error!";

                    } else {
                        errorMessage = "Oops. NoConnectionError!";
                    }

                } else if (error instanceof AuthFailureError) {

                    errorMessage = "Oops. AuthFailureError!";
                } else if (error instanceof ServerError) {

                    errorMessage = "Oops. ServerError!";
                } else if (error instanceof NetworkError) {
                    errorMessage = "Oops. NetworkError!";
                } else if (error instanceof ParseError) {
                    errorMessage = "Oops. ParseError!";
                }
                callback.onErrorResponse(errorMessage);
            }
        });

        // Adding request to request queue
        ApplicationController.getInstance().addToRequestQueue(billwiseReceiptRequest, REQ_TAG);

    }

    /**
     * Get DAILYPRODUCT Report from Web Server using Volley
     */
    public static void webDailyProductReport(final webObjectCallback callback, JSONObject object) {

        final String REQ_TAG = "volley_get_productwise_report";

        JsonObjectRequest getProductwiseRequest = new JsonObjectRequest(Request.Method.POST, ConfigURL.URL_GET_PRODUCTWISE_REPORT, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                callback.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                String errorMessage = null;

                if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                    if (error.getClass().equals(TimeoutError.class)) {
                        // Show timeout error message
                        errorMessage = "Oops. Timeout error!";

                    } else {
                        errorMessage = "Oops. NoConnectionError!";
                    }

                } else if (error instanceof AuthFailureError) {

                    errorMessage = "Oops. AuthFailureError!";
                } else if (error instanceof ServerError) {

                    errorMessage = "Oops. ServerError!";
                } else if (error instanceof NetworkError) {
                    errorMessage = "Oops. NetworkError!";
                } else if (error instanceof ParseError) {
                    errorMessage = "Oops. ParseError!";
                }
                callback.onErrorResponse(errorMessage);
            }
        });


        // Adding request to request queue
        ApplicationController.getInstance().addToRequestQueue(getProductwiseRequest, REQ_TAG);

    }

    /**
     * Get DAILYPRODUCT Report from Web Server using Volley
     */
    public static void webInvoicewiseReport(final webObjectCallback callback, JSONObject object) {

        final String REQ_TAG = "volley_get_invoicewise_report";

        JsonObjectRequest getInvoicewiseRequest = new JsonObjectRequest(Request.Method.POST, ConfigURL.URL_GET_INVOICEWISE_REPORT, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                callback.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                String errorMessage = null;

                if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                    if (error.getClass().equals(TimeoutError.class)) {
                        // Show timeout error message
                        errorMessage = "Oops. Timeout error!";

                    } else {
                        errorMessage = "Oops. NoConnectionError!";
                    }

                } else if (error instanceof AuthFailureError) {

                    errorMessage = "Oops. AuthFailureError!";
                } else if (error instanceof ServerError) {

                    errorMessage = "Oops. ServerError!";
                } else if (error instanceof NetworkError) {
                    errorMessage = "Oops. NetworkError!";
                } else if (error instanceof ParseError) {
                    errorMessage = "Oops. ParseError!";
                }
                callback.onErrorResponse(errorMessage);
            }
        });


        // Adding request to request queue
        ApplicationController.getInstance().addToRequestQueue(getInvoicewiseRequest, REQ_TAG);

    }
    /**
     * Save Order Web Server using Volley
     */
    public static void websave_saleorder(final webObjectCallback callback, JSONObject object) {

        final String REQ_TAG = "volley_save_saleorder_tag";

        JsonObjectRequest websave_saleorderRequest = new JsonObjectRequest(Request.Method.POST, ConfigURL.URL_SAVE_ORDER, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                callback.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                String errorMessage = null;

                if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                    if (error.getClass().equals(TimeoutError.class)) {
                        // Show timeout error message
                        errorMessage = "Oops. Timeout error!";

                    } else {
                        errorMessage = "Oops. NoConnectionError!";
                    }

                } else if (error instanceof AuthFailureError) {

                    errorMessage = "Oops. AuthFailureError!";
                } else if (error instanceof ServerError) {

                    errorMessage = "Oops. ServerError!";
                } else if (error instanceof NetworkError) {
                    errorMessage = "Oops. NetworkError!";
                } else if (error instanceof ParseError) {
                    errorMessage = "Oops. ParseError!";
                }
                callback.onErrorResponse(errorMessage);
            }
        });

        // Adding request to request queue
        ApplicationController.getInstance().addToRequestQueue(websave_saleorderRequest, REQ_TAG);


    }
    /**
     * Stock Transfer
     */
    public static void webGetStocktransferwithorderlist(final webObjectCallback callback, JSONObject object) {

        final String REQ_TAG = "volley_return_tag";

        JsonObjectRequest placeOrderRequest = new JsonObjectRequest(Request.Method.POST, URL_STOCK_FETCH_LIVE, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                callback.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                String errorMessage = null;

                if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                    if (error.getClass().equals(TimeoutError.class)) {
                        // Show timeout error message
                        errorMessage = "Oops. Timeout error!";

                    } else {
                        errorMessage = "Oops. NoConnectionError!";
                    }

                } else if (error instanceof AuthFailureError) {

                    errorMessage = "Oops. AuthFailureError!";
                } else if (error instanceof ServerError) {

                    errorMessage = "Oops. ServerError!";
                } else if (error instanceof NetworkError) {
                    errorMessage = "Oops. NetworkError!";
                } else if (error instanceof ParseError) {
                    errorMessage = "Oops. ParseError!";
                }
                callback.onErrorResponse(errorMessage);
            }
        });

        // Adding request to request queue
        ApplicationController.getInstance().addToRequestQueue(placeOrderRequest, REQ_TAG);


    }

    /**
     * Order Transfer
     */
    public static void webGetOrdertransferDetails(final webObjectCallback callback, JSONObject object) {

        final String REQ_TAG = "ordertransfer_tag";

        JsonObjectRequest placeOrderRequest = new JsonObjectRequest(Request.Method.POST, URL_ORDER_TRANSFER_DETAILS, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                callback.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                String errorMessage = null;

                if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                    if (error.getClass().equals(TimeoutError.class)) {
                        // Show timeout error message
                        errorMessage = "Oops. Timeout error!";

                    } else {
                        errorMessage = "Oops. NoConnectionError!";
                    }

                } else if (error instanceof AuthFailureError) {

                    errorMessage = "Oops. AuthFailureError!";
                } else if (error instanceof ServerError) {

                    errorMessage = "Oops. ServerError!";
                } else if (error instanceof NetworkError) {
                    errorMessage = "Oops. NetworkError!";
                } else if (error instanceof ParseError) {
                    errorMessage = "Oops. ParseError!";
                }
                callback.onErrorResponse(errorMessage);
            }
        });

        // Adding request to request queue
        ApplicationController.getInstance().addToRequestQueue(placeOrderRequest, REQ_TAG);


    }
}
