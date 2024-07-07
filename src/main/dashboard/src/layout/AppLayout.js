import { Outlet } from "react-router-dom";
import Navigation from "../Navigation";
import * as React from "react";
import AppStateContextProvider from "../AppState";

export default function AppLayout() {

    return (
        <AppStateContextProvider>
            <React.Fragment>
                <header>
                    <Navigation />
                    <div style={{height: "20px", width: "100%"}} />
                </header>
                <main style={{ margin: "10px" }}>
                    <React.Suspense>
                        <Outlet />
                    </React.Suspense>
                </main>
            </React.Fragment>
        </AppStateContextProvider>
    )

}