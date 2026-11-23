package com.quickmeds24.app.data.model

enum class PaymentMethod(
    val displayName: String,
    val subtitle: String,
    val iconEmoji: String
) {
    UPI_GPAY(
        displayName = "Google Pay (GPay)",
        subtitle = "Fast direct UPI deep link via Google Pay app",
        iconEmoji = "🟢"
    ),
    PHONEPE(
        displayName = "PhonePe UPI",
        subtitle = "Instant UPI checkout via PhonePe app",
        iconEmoji = "🟣"
    ),
    PAYTM(
        displayName = "Paytm UPI / Wallet",
        subtitle = "Pay directly via Paytm app",
        iconEmoji = "🔵"
    ),
    COD(
        displayName = "Cash on Delivery (COD)",
        subtitle = "Pay with Cash or UPI upon medicine delivery",
        iconEmoji = "💵"
    )
}
