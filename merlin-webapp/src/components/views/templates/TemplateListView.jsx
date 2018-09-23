import React from 'react';
import {PageHeader} from 'react-bootstrap';
import {connect} from 'react-redux'
import {listTemplatesIfNeeded} from '../../../actions';
import TemplateListFailed from './TemplateListFailed';
import Template from './Template';

class TemplateListView extends React.Component {

    render() {

        this.props.listTemplates();

        return (
            <div>
                <PageHeader>Templates</PageHeader>

                {
                    this.props.templates.failed ?
                        <TemplateListFailed/> :
                        (this.props.templates.loaded ? Object.keys(this.props.templates.list).map(key =>
                            <Template
                                key={key}
                                id={key}
                                name={this.props.templates.list[key].name}
                                description={this.props.templates.list[key].description}
                            />
                        ) : <i>Loading...</i>)
                }
            </div>
        );
    }
}

const mapStateToProps = state => ({
    templates: state.templates
});

const actions = {
    listTemplates: listTemplatesIfNeeded
};

export default connect(mapStateToProps, actions)(TemplateListView);
