package com.canhub.canhub;
import okhttp3.OkHttpClient;

public class Supabase {
    // IMPORTANTE: Nunca expongas la SERVICE_KEY en aplicaciones cliente
    private static final String SUPABASE_URL = "https://pzlqlnjkzkxaitkphclx.supabase.co";
    private static final String SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InB6bHFsbmpremt4YWl0a3BoY2x4Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3Mzk0NDM2ODcsImV4cCI6MjA1NTAxOTY4N30.LybznQEqaU6dhIxuFI_SUygPNV_br1IAta099oWQuDc"; // Tu clave pública ANON

    private static OkHttpClient client;

    public static OkHttpClient getClient() {
        if (client == null) {
            client = new OkHttpClient.Builder()
                    .retryOnConnectionFailure(true)
                    .build();
        }
        return client;
    }

    public static String getSupabaseUrl() {
        return SUPABASE_URL;
    }

    public static String getSupabaseKey() {
        return SUPABASE_KEY;
    }
}