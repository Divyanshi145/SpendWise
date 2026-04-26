package com.example.expensetracker.utils;

import android.util.Log;
import com.example.expensetracker.data.TransactionInfo;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TransactionParser {

    // Regex to handle symbols: Rs, Rs., INR, ₹ and amounts with commas/decimals
    private static final String CURRENCY = "(?:Rs\\.?|INR|₹)";
    private static final String AMOUNT = "([\\d,]+\\.?\\d*)";
    private static final String CURRENCY_AMOUNT = "(?i)" + CURRENCY + "\\s?" + AMOUNT;

    private static final String[] DEBIT_PATTERNS = {
            // UPI SENT patterns
            CURRENCY_AMOUNT + "\\s?sent to\\s?([^\\s,.]+)",
            "transferred\\s?" + CURRENCY_AMOUNT + "\\s?to\\s?([^\\s,.]+)",
            "Paid\\s?" + CURRENCY_AMOUNT + "\\s?to\\s?([^\\s,.]+)",
            "UPI/P2P/" + AMOUNT + "/sent",
            
            // Bank debit patterns
            CURRENCY_AMOUNT + "\\s?debited",
            "debited.*?by\\s?" + CURRENCY_AMOUNT,
            "debited.*?with\\s?" + CURRENCY_AMOUNT,
            "debited.*?from.*?\\s?" + CURRENCY_AMOUNT,
            "your a/c.*?debited\\s?" + CURRENCY_AMOUNT,
            "withdrawal of\\s?" + CURRENCY_AMOUNT,
            "inr\\s?" + AMOUNT + "\\s?has been debited",
            "debited\\s?inr\\s?" + AMOUNT + "\\s?on",
            "Account.*?debited\\s?by\\s?" + CURRENCY_AMOUNT,
            "Account.*?debited\\s?for\\s?" + CURRENCY_AMOUNT,
            
            // Paytm/PhonePe/GPay patterns
            "You paid\\s?" + CURRENCY_AMOUNT + "\\s?to\\s?([^\\s,.]+)",
            "Payment of\\s?" + CURRENCY_AMOUNT + "\\s?successful",
            "Sent\\s?" + CURRENCY_AMOUNT + "\\s?successfully",
            "spent\\s?" + CURRENCY_AMOUNT + "\\s?on\\s?([^\\s,.]+)",
            
            // ₹ specific patterns
            "₹\\s?" + AMOUNT + "\\s?paid to\\s?([^\\s,.]+)"
    };

    public static TransactionInfo parseTransaction(String smsBody, String sender) {
        // Logging for debugging
        Log.d("SmsReceiver", "SMS received: " + smsBody);
        
        for (String patternStr : DEBIT_PATTERNS) {
            Pattern pattern = Pattern.compile(patternStr, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(smsBody);

            if (matcher.find()) {
                try {
                    String amountStr = matcher.group(1).replace(",", "");
                    double amount = Double.parseDouble(amountStr);
                    
                    // Logging parsed amount
                    Log.d("SmsReceiver", "Parsed amount: " + amount);

                    String merchant = "Bank Transaction";
                    
                    if (patternStr.contains("UPI/P2P/")) {
                        merchant = "UPI Transfer";
                    } else if (matcher.groupCount() >= 2 && matcher.group(2) != null) {
                        merchant = matcher.group(2).trim();
                    } else {
                        merchant = extractMerchant(smsBody);
                    }

                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    String date = df.format(Calendar.getInstance().getTime());

                    return new TransactionInfo(amount, sender, merchant, date, smsBody);
                } catch (Exception e) {
                    Log.e("TransactionParser", "Error parsing SMS: " + e.getMessage());
                }
            }
        }
        return null;
    }

    private static String extractMerchant(String smsBody) {
        String lowerSms = smsBody.toLowerCase();
        String[] keywords = {" sent to ", " paid to ", " transferred to ", " spent on ", " at ", " to "};
        for (String keyword : keywords) {
            if (lowerSms.contains(keyword)) {
                int index = lowerSms.indexOf(keyword);
                String sub = smsBody.substring(index + keyword.length()).trim();
                // Extract first word, filtering out punctuation
                return sub.split("[\\s,.]")[0];
            }
        }
        return "Bank Transaction";
    }
}
