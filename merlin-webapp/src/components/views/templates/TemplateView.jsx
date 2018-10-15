import React from 'react';
import {Alert, Nav, NavLink, TabContent, Table, TabPane} from 'reactstrap';
import {formatDateTime, getRestServiceUrl} from '../../../utilities/global';
import classNames from 'classnames';
import {PageHeader} from '../../general/BootstrapComponents';
import LinkFile from '../../general/LinkFile';
import TemplateDefinition from './templatedefinition/TemplateDefinition';
import TemplateRunTab from './TemplateRunTab';
import TemplateSerialRunTab from './TemplateSerialRunTab';

class TemplateView extends React.Component {

    state = {
        primaryKey: this.props.match.params.primaryKey,
        activeTab: '1',
        loading: true
    };

    componentDidMount = () => {
        fetch(getRestServiceUrl('templates/template', {
            primaryKey: this.state.primaryKey
        }), {
            method: 'GET',
            headers: {
                'Accept': 'application/json'
            }
        })
            .then(response => {
                return response.json();
            })
            .then((json) => this.setState({
                loading: false,
                template: json
            }))
            .catch(() => this.setState({
                loading: false,
                error: true
            }));
    };

    toggleTab = tab => () => this.setState({
        activeTab: tab
    });

    render = () => {

        if (this.state.loading) {
            return <Alert color={'secondary'}>
                Loading Template {this.state.primaryKey}
            </Alert>;
        }

        if (this.state.error) {
            return <Alert color={'danger'}>
                No template found
            </Alert>
        }
        const template = this.state.template;
        let templateId = template.id ? template.id : template.filename;
        return (
            <div>
                <PageHeader>
                    Template: {templateId}
                </PageHeader>
                <Nav tabs>
                    <NavLink
                        className={classNames({active: this.state.activeTab === '1'})}
                        onClick={this.toggleTab('1')}
                    >
                        Main
                    </NavLink>
                    {this.state.template.templateDefinition ?
                        <NavLink
                            className={classNames({active: this.state.activeTab === '2'})}
                            onClick={this.toggleTab('2')}
                        >
                            Template Definition
                        </NavLink> : undefined}
                    <NavLink
                        className={classNames({active: this.state.activeTab === '3'})}
                        onClick={this.toggleTab('3')}
                    >
                        Run
                    </NavLink>
                    <NavLink
                        className={classNames({active: this.state.activeTab === '4'})}
                        onClick={this.toggleTab('4')}
                    >
                        Serial run
                    </NavLink>
                </Nav>
                <TabContent activeTab={this.state.activeTab}>
                    <TabPane tabId={'1'}>
                        <Table hover>
                            <tbody>
                            <tr>
                                <td>Filename</td>
                                <td>{this.state.template.fileDescriptor.filename}</td>
                            </tr>
                            <tr>
                                <td>Last modified</td>
                                <td>{formatDateTime(this.state.template.fileDescriptor.lastModified)}</td>
                            </tr>
                            <tr>
                                <td>Path</td>
                                <td><LinkFile primaryKey={template.fileDescriptor.primaryKey}
                                              filepath={template.fileDescriptor.canonicalPath}/></td>
                            </tr>
                            </tbody>
                        </Table>
                    </TabPane>
                    {this.state.template.templateDefinition ?
                        <TabPane tabId={'2'}>
                            <TemplateDefinition
                                match={{
                                    params: {
                                        primaryKey: this.state.template.templateDefinition.fileDescriptor.primaryKey
                                    }
                                }}
                            />
                        </TabPane> : undefined}
                    <TabPane tabId={'3'}>
                        <TemplateRunTab
                            primaryKey={this.state.primaryKey}
                            templateDefinitionId={this.state.template.templateDefinition ?
                                this.state.template.templateDefinition.fileDescriptor.primaryKey : ''}
                            variableDefinitions={this.state.template.templateDefinition ?
                                this.state.template.templateDefinition.variableDefinitions :
                                this.state.template.statistics.usedVariables}
                        />
                    </TabPane>
                    <TabPane tabId={'4'}>
                        <TemplateSerialRunTab
                            primaryKey={this.state.primaryKey}
                            templateDefinitionId={this.state.template.templateDefinition ?
                                this.state.template.templateDefinition.fileDescriptor.primaryKey : ''}
                            variableDefinitions={this.state.template.templateDefinition ?
                                this.state.template.templateDefinition.variableDefinitions :
                                this.state.template.statistics.usedVariables}
                        />
                    </TabPane>
                </TabContent>
            </div>
        );
    };

    constructor(props) {
        super(props);

        this.toggleTab = this.toggleTab.bind(this);
    }
}


export default TemplateView;