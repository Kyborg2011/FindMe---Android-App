package com.luceolab.me.data;

import com.luceolab.me.data.RestCallback;

public interface Command {

    // New network hit
    void fresh(RestCallback callback);

    // First cached if available then fresh
    void all(RestCallback callback);

    // Cached response only
    void cached(RestCallback callback);

    // Cancel the request
    void cancel();

}
