import React from 'react';
import {FormGroup, Nav, NavLink, TabContent, TabPane} from 'reactstrap';
import {Redirect} from 'react-router-dom'
import {PageHeader} from '../../general/BootstrapComponents';
import {FormButton, FormField} from '../../general/forms/FormComponents';
import {isDevelopmentMode} from "../../../utilities/global";
import I18n from "../../general/translation/I18n";
import classNames from "classnames";
import ConfigAccountTab from "./ConfigurationAccountTab";
import ConfigServerTab from "./ConfigurationServerTab";

class ConfigurationPage
    extends React
        .Component {

    constructor(props) {
        super(props);

        this.state = {
            activeTab: '1',
            redirect: false
        };

        this.onSave = this.onSave.bind(this);
        this.onCancel = this.onCancel.bind(this);
    }

    toggleTab = tab => () => {
        this.setState({
            activeTab: tab
        })
    };

    setRedirect = () => {
        this.setState({
            redirect: true
        })
    }

    onSave(event) {
        this.serverTab.save();
        this.accountTab.save();
        this.setRedirect();
    }

    onCancel() {
        this.setRedirect()
    }

    render() {
        // https://codepen.io/_arpy/pen/xYoyPW
        if (this.state.redirect) {
            return <Redirect to='/'/>
        }

        let todo = '';
        if (isDevelopmentMode()) {
            todo = <code><h3>ToDo</h3>
                <ul>
                    <li>Do the form validation (server and/or client side) with error fields.</li>
                </ul>
            </code>
        }
        return (
            <React.Fragment>
                <PageHeader><I18n name={'configuration'}/></PageHeader>
                <Nav tabs>
                    <NavLink
                        className={classNames({active: this.state.activeTab === '1'})}
                        onClick={this.toggleTab('1')}
                    >
                        <I18n name={'configuration.account'}/>
                    </NavLink>
                    <NavLink
                        className={classNames({active: this.state.activeTab === '2'})}
                        onClick={this.toggleTab('2')}
                    >
                        <I18n name={'configuration.server'}/>
                    </NavLink>
                </Nav>
                <TabContent activeTab={this.state.activeTab}>
                    <TabPane tabId={'1'}>
                        <ConfigAccountTab onRef={ref => (this.accountTab = ref)}/>
                    </TabPane>
                </TabContent>
                <TabContent activeTab={this.state.activeTab}>
                    <TabPane tabId={'2'}>
                        <ConfigServerTab onRef={ref => (this.serverTab = ref)}/>
                    </TabPane>
                </TabContent>
                <FormGroup>
                    <FormField length={12}>
                        <FormButton onClick={this.onCancel}
                                    hintKey="configuration.cancel.hint"><I18n name={'common.cancel'}/>
                        </FormButton>
                        <FormButton onClick={this.onSave} bsStyle="primary"
                                    hintKey="configuration.save.hint"><I18n name={'common.save'}/>
                        </FormButton>
                    </FormField>
                </FormGroup>
                {todo}
            </React.Fragment>
        );
    }
}

export default ConfigurationPage;

