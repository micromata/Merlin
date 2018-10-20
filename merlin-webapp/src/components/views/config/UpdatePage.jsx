import React from 'react';
import {connect} from 'react-redux';
import {PageHeader} from '../../general/BootstrapComponents';
import {getRestServiceUrl} from '../../../utilities/global';
import {FormButton} from '../../general/forms/FormComponents';


class UpdatePage extends React.Component {

    onUpdate() {
        fetch(getRestServiceUrl('updates/update'), {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        });
    }

    render() {
        let content = 'No new version available.';
        if (this.props.updateVersion) {
            content = <React.Fragment>
                <h2>New Version</h2>
                <form>
                    <FormButton
                        onClick={this.onUpdate}
                        hint="Download and starts update."
                    >
                        Update
                    </FormButton>
                </form>
            </React.Fragment>
        }
        return <React.Fragment>
            <PageHeader>Configuration</PageHeader>
            {content}
        </React.Fragment>;
    }
}

const mapStateToProps = state => ({
    updateVersion: state.version.updateVersion
});

export default connect(mapStateToProps, {})(UpdatePage);

