# Polling vs Webhook

## Introduction

When two applications need to communicate with each other, one common requirement is notifying another system when some data changes or when a specific event occurs.

For example:

* A payment gateway needs to notify an e-commerce application when a payment is completed.
* A food ordering system needs to notify a delivery application when food is ready.
* GitHub needs to notify a CI/CD pipeline when code is pushed to a repository.
* A shipping provider needs to notify an online store when an order is delivered.

There are two common approaches to achieve this communication:

1. Polling
2. Webhook

Although both approaches solve the same problem, they work in very different ways and have different trade-offs.

[![](https://markdown-videos-api.jorgenkh.no/youtube/wxe3DaEiZcI)](https://youtu.be/wxe3DaEiZcI)

# What is Polling?

Polling is a communication technique in which a client repeatedly sends requests to a server at regular intervals to check whether new information is available.

In simple terms, the client continuously asks "Has anything changed?"

until it receives the expected response.

## Example

Consider a food delivery application. A customer places an order and wants to know when the food is ready. The delivery application can repeatedly call the restaurant service every few seconds:

```text
Is the food ready?
```

Restaurant Service:

```text
No
```

After 5 seconds:

```text
Is the food ready?
```

Restaurant Service:

```text
No
```

After another 5 seconds:

```text
Is the food ready?
```

Restaurant Service:

```text
Yes
```

This repeated checking is known as Polling.

# Polling Workflow

```text
Client
   |
   |---- GET /status
   |
Server
   |
   |---- PREPARING

(wait)

Client
   |
   |---- GET /status
   |
Server
   |
   |---- PREPARING

(wait)

Client
   |
   |---- GET /status
   |
Server
   |
   |---- READY
```

The client is responsible for initiating all communication.

# Characteristics of Polling

* Client-driven communication
* Repeated requests at fixed intervals
* Easy to implement
* Suitable for simple systems
* Can generate unnecessary traffic

# Advantages of Polling

## 1. Simple Implementation

Polling is easy to implement because it only requires a regular API endpoint. The client can periodically call the endpoint and check for updates.

Example:

```java
while(true) {

    String status =
        restTemplate.getForObject(
            "/status",
            String.class);

    if("READY".equals(status)) {
        break;
    }

    Thread.sleep(5000);
}
```

## 2. Easy to Debug

Since the client initiates all requests, debugging is straightforward. Developers can easily inspect requests and responses.

## 3. Works with Any System

Polling does not require the server to support callbacks or event notifications. As long as an API endpoint is available, polling can be implemented.


# Disadvantages of Polling

## 1. Unnecessary Requests

Many requests may return exactly the same response.

Example:

```text
Food Preparation Time = 20 seconds

Polling Interval = 5 seconds
```

Requests:

```text
Request 1 -> PREPARING
Request 2 -> PREPARING
Request 3 -> PREPARING
Request 4 -> PREPARING
Request 5 -> READY
```

Most requests provided no new information.

## 2. Increased Server Load

The server must process every polling request even if no data has changed. In large-scale systems, this can significantly increase CPU and memory usage.

## 3. Increased Network Traffic

Polling generates continuous network traffic. Thousands of clients polling simultaneously can create a large number of unnecessary requests.


## 4. Delayed Updates

Updates are only received during the next polling cycle.

Example:

```text
Polling Interval = 10 seconds

Food Ready At:
10:00:01

Next Poll:
10:00:10
```

The client receives the update 9 seconds later.

# What is a Webhook?

A Webhook is an event-driven communication mechanism in which one application automatically sends an HTTP request to another application when a specific event occurs.

Instead of continuously asking for updates, the receiving application provides a callback URL. When the event happens, the sender calls that URL and delivers the information.

# Example

Using the same food-ordering scenario:

The delivery application provides a callback URL:

```text
POST /order-ready
```

When the food becomes ready, the restaurant service automatically sends a request:

```text
POST /order-ready
```

Payload:

```json
{
  "status": "READY"
}
```

The delivery application immediately receives the update.


# Webhook Workflow

```text
Food Ready
     |
Restaurant Service
     |
     |---- POST /order-ready
     |
Delivery Application
```

Communication happens only when an event occurs.


# Characteristics of Webhooks

* Event-driven communication
* Server initiates communication
* Real-time updates
* Fewer requests
* Better scalability


# Advantages of Webhooks

## 1. Real-Time Notifications

Updates are delivered immediately when an event occurs. There is no waiting period.


## 2. Reduced Network Traffic

Requests are only sent when something important happens. No unnecessary status checks are required.


## 3. Lower Server Load

The sender processes fewer requests compared to polling. This improves overall system efficiency.


## 4. Better Scalability

Webhook-based systems generally scale better because they avoid continuous status checking. This becomes especially important when handling millions of users.


## 5. Event-Driven Architecture

Webhooks fit naturally into modern event-driven systems and microservice architectures.

# Disadvantages of Webhooks

## 1. More Complex Setup

The receiving application must expose an endpoint capable of receiving webhook events.

Example:

```java
@PostMapping("/order-ready")
public String orderReady() {
    return "SUCCESS";
}
```


## 2. Retry Handling

What happens if the receiving application is unavailable? The sender must implement retry logic.

Example:

```text
Webhook Failed

Retry After 30 Seconds
```

Without retries, events may be lost.

## 3. Security Challenges

Webhook endpoints are publicly accessible.

Applications often implement:

* API Keys
* HMAC Signatures
* JWT Tokens
* IP Whitelisting

to verify the authenticity of requests.

## 4. Duplicate Events

The same webhook may be delivered multiple times. Applications should be designed to process events idempotently.

Example:

```java
if(eventAlreadyProcessed) {
    return;
}
```


# Polling vs Webhook

| Feature                    | Polling        | Webhook        |
| -------------------------- | -------------- | -------------- |
| Communication Initiated By | Client         | Server         |
| Requests                   | Continuous     | Event Based    |
| Network Usage              | High           | Low            |
| Server Load                | High           | Low            |
| Real-Time Updates          | No             | Yes            |
| Scalability                | Lower          | Higher         |
| Implementation Complexity  | Simple         | Moderate       |
| Resource Utilization       | Less Efficient | More Efficient |


# Real-World Use Cases of Polling

Polling is commonly used when:

* The external system does not support webhooks.
* Simplicity is preferred over efficiency.
* Update frequency is low.
* Temporary monitoring is required.

Examples:

* Checking job status
* Monitoring background tasks
* Refreshing dashboards
* Legacy system integrations


# Real-World Use Cases of Webhooks

Webhooks are widely used in modern distributed systems.

Examples:

## Payment Systems

When payment status changes:

```text
Payment Successful
Payment Failed
Refund Processed
```

the payment provider sends a webhook notification.

Examples:

* Stripe
* Razorpay
* PayPal


## GitHub

GitHub sends webhooks when:

* Code is pushed
* Pull requests are created
* Pull requests are merged


## CI/CD Pipelines

A webhook can automatically trigger a build when code is committed.

## Shipping Systems

Delivery providers notify e-commerce applications when:

* Order shipped
* Order delivered
* Order returned

# When Should We Use Polling?

Use Polling when:

* Webhook support is unavailable.
* Simplicity is important.
* Real-time updates are not required.
* The number of clients is small.

# When Should We Use Webhooks?

Use Webhooks when:

* Real-time updates are required.
* High scalability is important.
* Network efficiency matters.
* Event-driven communication is preferred.

# Conclusion

Polling and Webhooks are two different approaches to solving the same problem: keeping systems synchronized when data changes.

Polling works by repeatedly asking for updates, making it simple but less efficient.

Webhooks work by automatically pushing updates when events occur, making them more scalable and suitable for modern applications.

In small systems, polling may be sufficient. However, for large-scale and real-time applications, webhooks are generally the preferred solution because they reduce unnecessary requests, lower server load, and provide immediate event notifications.