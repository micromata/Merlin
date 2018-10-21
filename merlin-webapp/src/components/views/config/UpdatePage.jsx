import React from 'react';
import {connect} from 'react-redux';
import {PageHeader} from '../../general/BootstrapComponents';
import {getRestServiceUrl} from '../../../utilities/global';
import {FormButton} from '../../general/forms/FormComponents';


class UpdatePage extends React.Component {

    onUpdate() {
        fetch(getRestServiceUrl('updates/install'), {
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
                The new version {this.props.updateVersion} is available.
                <br/>
                You can start the update process by simply clicking the update button.
                <br/>
                <br/>
                <form>
                    <FormButton
                        onClick={this.onUpdate}
                        hint="Download and starts update."
                    >
                        Update
                    </FormButton>
                </form>
                <br/>
                The update button functions only if your browser is running on the same computer as your Merlin server.
                <br/>
                If the installer doesn't start after clicking the update button, please proceed manually by downloading
                the installer of the new version:
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

