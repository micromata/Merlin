import React from 'react';
import {getRestServiceUrl, isDevelopmentMode} from "../../../../utilities/global";
import {
    TabContent,
    TabPane,
    Nav,
    NavLink
} from 'reactstrap';
import classNames from 'classnames';
import ErrorAlert from "../../../general/ErrorAlert";
import {PageHeader} from "../../../general/BootstrapComponents";
import TemplateDefinitionMain from "./TemplateDefinitionMain";
import TemplateDefinitionVariables from "./TemplateDefinitionVariables";
import TemplateDefinitionDependentVariables from "./TemplateDefinitionDependentVariables";
import LogEmbeddedPanel from "../../logging/LogEmbeddedPanel";

class TemplateDefinitionPage extends React.Component {
    componentDidMount = () => {
        this.fetchTemplateDefinition();
    };

    handleTextChange = (value, name) => {
        // Read-only, do nothing.
        //let definition = this.state.definition;
        //definition[name] = value;
        //this.setState({definition: definition});
    }

    handleStateChange = event => {
        // Read-only, do nothing.
        //let definition = this.state.definition;
        //definition[event.target.name] = event.target.checked;
        //this.setState({definition: definition});
    }

    handleVariableTextChange = (value, name, index) => {
        // Read-only, do nothing.
        //let definition = this.state.definition;
        //const variable = definition.variableDefinitions[index];
        //variable[name] = value;
        //this.setState({definition: definition});
    }

    handleVariableStateChange = (event, index) => {
        // Read-only, do nothing.
        //let definition = this.state.definition;
        //const variable = definition.variableDefinitions[index];
        //variable[event.target.name] = event.target.checked;
        //this.setState({definition: definition});
    }

    fetchTemplateDefinition = () => {
        this.setState({
            isFetching: true,
            failed: false,
            definition: undefined
        });
        fetch(getRestServiceUrl("templates/definition", {
            primaryKey: this.state.primaryKey
        }), {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        })
            .then(response => response.json())
            .then(json => {
                this.setState({
                    isFetching: false,
                    definition: json
                });
            })
            .catch(() => this.setState({isFetching: false, failed: true}));
    };

    render = () => {
        let content = undefined;
        if (this.state.isFetching) {
            content = <i>Loading...</i>;
        } else if (this.state.failed) {
            content = <ErrorAlert
                title={'Cannot load Template Definition'}
                description={'Something went wrong during contacting the rest api.'}
                action={{
                    handleClick: this.fetchTemplateDefinition,
                    title: 'Try again'
                }}
            />;
        } else if (this.state.definition) {
            content = <React.Fragment>
                <Nav tabs>
                    <NavLink
                        className={classNames({active: this.state.activeTab === '1'})}
                        onClick={() => {
                            this.toggleTab('1');
                        }}>
                        Main
                    </NavLink>
                    <NavLink
                        className={classNames({active: this.state.activeTab === '2'})}
                        onClick={() => {
                            this.toggleTab('2');
                        }}>
                        Variables
                    </NavLink>
                    <NavLink
                        className={classNames({active: this.state.activeTab === '3'})}
                        onClick={() => {
                            this.toggleTab('3');
                        }}>
                        Dependent variables
                    </NavLink>
                    <NavLink
                        className={classNames({active: this.state.activeTab === '4'})}
                        onClick={() => {
                            this.toggleTab('4');
                        }}>
                        Logging
                    </NavLink>
                </Nav>
                <TabContent activeTab={this.state.activeTab}>
                    <TabPane tabId="1">
                        <TemplateDefinitionMain definition={this.state.definition}
                                                handleTextChange={this.handleTextChange}
                                                handleStateChange={this.handleStateChange}/>
                    </TabPane>
                    <TabPane tabId="2">
                        <TemplateDefinitionVariables definition={this.state.definition}
                                                     handleTextChange={this.handleVariableTextChange}
                                                     handleStateChange={this.handleVariableStateChange}/>
                    </TabPane>
                    <TabPane tabId="3">
                        <TemplateDefinitionDependentVariables definition={this.state.definition}/>
                    </TabPane>
                    <TabPane tabId={'4'}>
                        <LogEmbeddedPanel
                            mdcTemplateDefinitionPk={this.state.primaryKey}
                        />
                    </TabPane>
                </TabContent>

            </React.Fragment>;
        }

        let todo = '';
        if (isDevelopmentMode()) {
            todo = <code><h3>ToDo</h3>
                <ul>
                    <li>Auto grow of textarea.</li>
                </ul>
            </code>
        }
        let title = this.state.definition ? this.state.definition.id : 'Template definition';
        let pageHeader = !this.props.hidePageHeader ? <PageHeader>
            {title}
        </PageHeader> : null;

        return <React.Fragment>
            {pageHeader}
            {content}
            {todo}
        </React.Fragment>;
    };

    constructor(props) {
        super(props);
        this.state = {
            primaryKey: this.props.match.params.primaryKey,
            isFetching: true,
            failed: false,
            definition: undefined,
            activeTab: '1'
        };
        this.handleTextChange = this.handleTextChange.bind(this);
        this.handleStateChange = this.handleStateChange.bind(this);
        this.handleVariableStateChange = this.handleVariableStateChange.bind(this);
        this.handleVariableTextChange = this.handleVariableTextChange.bind(this);
        this.fetchTemplateDefinition = this.fetchTemplateDefinition.bind(this);
        this.toggleTab = this.toggleTab.bind(this);
    }

    toggleTab(tab) {
        if (this.state.activeTab !== tab) {
            this.setState({
                activeTab: tab
            });
        }
    }
}

export default TemplateDefinitionPage;