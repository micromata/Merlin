import React from 'react';
import createBrowserHistory from 'history/createBrowserHistory';
import {Route, Router, Switch} from 'react-router';
import Menu from "./general/Menu";
import Start from "./views/Start";
import View from "./views/config/View";
import TableExample from "./views/TableExample";
import DropAreaExample from "./views/DropAreaExample";

class App extends React.Component {

    render() {
        const history = createBrowserHistory();
        const routes = [
            ['Start', '/', Start],
            ['Config', '/config', View],
            ['Table Example', '/table', TableExample],
            ['Drop Area Example', '/drop', DropAreaExample]
        ];

        return (
            <Router history={history}>
                <div>
                    <Menu routes={routes}/>
                    <div className={'container'}>
                        <Switch>
                            {
                                routes.map((route, index) => (
                                    <Route
                                        key={index}
                                        path={route[1]}
                                        component={route[2]}
                                        exact
                                    />
                                ))
                            }
                        </Switch>
                    </div>
                </div>
            </Router>
        );
    }
}

export default App;
