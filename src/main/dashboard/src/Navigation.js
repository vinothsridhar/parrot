import { WechatOutlined } from "@ant-design/icons";
import { Menu } from "antd";
import { useState } from "react";
import React from "react";

const items = [
    {
        label: "Chat Bots",
        key: "chat-bots",
        icon: <WechatOutlined />
    }
]

export default function Navigation() {

    const [current, setCurrent] = useState('chat-bots');

    const handleMenuClick = (e) => {
        setCurrent(e.key)
    }

    return (
        <Menu onClick={handleMenuClick} items={items} mode="horizontal" selectedKeys={[current]} />
    )

}