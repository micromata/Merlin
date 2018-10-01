import React from 'react'
import {PageHeader} from 'react-bootstrap';
import {getRestServiceUrl} from "../../../actions/global";
import ErrorAlert from "../../general/ErrorAlert";
import Template from "./Template";

class TemplateListView extends React.Component {


    path = getRestServiceUrl('templates');
    state = {
        isFetching: false
    };

    componentDidMount = () => {
        this.fetchTemplates();
    };

    fetchTemplates = () => {
        this.setState({
            isFetching: true,
            failed: false,
            templates: undefined
        });

        fetch(`${this.path}/list`, {
            method: 'GET',
            headers: {
                'Accept': 'application/json'
            }
        })
            .then(response => response.json())
            .then(json => {
                console.log(json);
                this.setState({
                    isFetching: false,
                    templates: json.templates.map(template => {
                        let templateDefinition, name;

                        if (typeof template.templateDefinition === 'object') {
                            templateDefinition = template.templateDefinition;
                            name = template.fileDescriptor.filename;
                        } else {
                            templateDefinition = json.templateDefinitions.filter(definition =>
                                definition.refId === template.templateDefinition)[0];
                            name = templateDefinition.name;
                        }

                        return {
                            name,
                            description: templateDefinition.description,
                            fileDescriptor: template.fileDescriptor
                        };
                    })
                })
            })
            .catch(() => this.setState({isFetching: false, failed: true}));
    };

    render = () => {
        let content = undefined;

        if (this.state.isFetching) {
            content = <i>Loading...</i>;
        } else if (this.state.failed) {
            content = <ErrorAlert
                title={'Cannot load Templates'}
                description={'Something went wrong during contacting the rest api.'}
                action={{
                    handleClick: this.fetchTemplates,
                    title: 'Try again'
                }}
            />;
        } else if (this.state.templates) {
            content = <div>
                {this.state.templates.map(template => <Template
                    key={template.name}
                    {...template}
                />)}
            </div>;
            console.log(this.state.templates);
        }

        return <div>
            <PageHeader>
                Templates
            </PageHeader>
            {content}
        </div>;
    };

    constructor(props) {
        super(props);

        this.fetchTemplates = this.fetchTemplates.bind(this);
    }
}

export default TemplateListView;