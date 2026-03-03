# Shorty Pro

#### Distributed URL Shortener with Analytics

## Overview

### Core Functionality

A high-performance URL shortener that generates short links and provides real-time analytics on click patterns. This domain is perfect for testing high-throughput read/write operations and data aggregation.

### Simple API Endpoints:

* `POST /shorten`: Accepts a long URL, returns a short code 
* `GET /{shortCode}`: Redirects to original URL (high traffif endpoint)
* `GET /analytics/{shortCode}`: Returns click statistics and geographic data

