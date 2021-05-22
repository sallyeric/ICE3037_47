import slack
slack_token = "xoxb-2099755807972-2117396446624-As8Te75GPRg2J5YsQvQFdMnq" # 발급받은 Token 값
client = slack.WebClient(token=slack_token)
client.chat_postMessage(channel="#종설프", text="Hello world!")