# scrooge-v2
## Endpoint
### POST /events/_create
request
```
{
    "name": "Koiki Camp",    ... (required)
    "transferCurrency": "JPY"        ... (required)
}
```

response (201 status)
```
{
    "name": "Koiki Camp",
    "id": "5a226c2d7c245e14f33fc5a8",
    "createdAt": "2017-12-02T16:52:45.52",
    "updatedAt": "2017-12-02T16:52:45.52",
    "transferCurrency": "JPY",
    "groups": [
        {
            "id": "...",
            "name": "...",
            "scrooges": [],
            "members": []
        }
    ],
    "transferAmounts": []
}
```

### POST /events/{eventId}/groups/_add
request
```
{
    "name": "Koiki Camp",    ... (required)
}
```

### PATCH /events/{eventId}/groups/{groupId}/_name
request
```
{
    "name": "Koiki Camp",    ... (required)
}
```

### DELETE /events/{eventId}/groups/{groupId}
wip

### POST /events/{eventId}/groups/{groupId}/memberNames/{memberName}
WIP

### POST /events/{eventId}/groups/{groupId}/scrooges/_add
```
{
    "memberName": "Nabnab", ... (required)
    "paidAmount": 200,      ... (required)
    "currency": "JPY",      ... (required)
    "forWhat": "rent-a-car" ... (optional)
}
```

### GET /events/{eventId}
response
```
{
    "name": "Koiki Camp",
    "id": "5a226c2d7c245e14f33fc5a8",
    "createdAt": "2017-12-02T16:52:45.52",
    "updatedAt": "2017-12-02T16:52:45.52",
    "transferCurrency": "JPY",
    "groups": [
        {
            "name": "Default",
            "memberNames": ["Nabnab", "Ninja"],
            "scrooges": [
                {
                    "memberName": "Nabnab",
                    "paidAmount": 200,
                    "forWhat": "rent-a-car",
                    "id": "5a226c2d7c245e14f33fc5a8",
                    "eventId": "5a226c2d7c245e14f33fc5a8",
                    "createdAt": "2017-12-02T16:52:45.52",
                    "updatedAt": "2017-12-02T16:52:45.52",
                    "currency": "JPY"
                },
                {
                    "memberName": "Ninja",
                    "paidAmount": 500,
                    "forWhat": "beef",
                    "id": "5a226c2d7c245e14f33fc5a8",
                    "eventId": "5a226c2d7c245e14f33fc5a8",
                    "createdAt": "2017-12-02T16:52:45.52",
                    "updatedAt": "2017-12-02T16:52:45.52",
                    "currency": "JPY"
                }
            ],
            "transferAmounts": [
                {
                    "from": "Nabnab",
                    "to": "Ninja",
                    "amount": 150
                }
            ]
        }
    ]
}
```