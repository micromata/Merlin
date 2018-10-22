import React from 'react';
import {connect} from 'react-redux';
import {PageHeader} from '../../general/BootstrapComponents';
import {getRestServiceUrl} from '../../../utilities/global';
import {FormButton} from '../../general/forms/FormComponents';
import {Table} from 'reactstrap';


class UpdatePage extends React.Component {

    onUpdate = () => {
        fetch(getRestServiceUrl('updates/install'), {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        });
    }

    componentDidMount() {
        fetch(getRestServiceUrl("updates/info"), {
            method: "GET",
            dataType: "JSON",
            headers: {
                "Content-Type": "text/plain; charset=utf-8"
            }
        })
            .then((resp) => {
                return resp.json();
            })
            .then((data) => {
                this.setState({
                    version: data.version,
                    installerUrl: data.installerUrl,
                    fileSize: data.fileSize,
                    filename: data.filename,
                    baseUrl: data.baseUrl,
                    comment: data.comment
                });
            })
            .catch((error) => {
                console.log(error, "Oups, what's happened?")
            })
    }

    render() {
        let content = 'No new version available.';
        if (this.props.updateVersion) {
            let comment = null;
            if (this.state.comment) {
                comment = <tr>
                    <th>Comment</th>
                    <td>{this.state.comment}</td>
                </tr>;
            }
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
                <br/>
                <Table striped bordered hover size={'sm'}>
                    <tbody>
                    <tr>
                        <th>Version</th>
                        <td>{this.state.version}</td>
                    </tr>
                    <tr>
                        <th>Installer url</th>
                        <td><a href={this.state.installerUrl} target={'_new'}>{this.state.installerUrl}</a></td>
                    </tr>
                    <tr>
                        <th>File size</th>
                        <td>{this.state.fileSize}</td>
                    </tr>
                    <tr>
                        <th>File name</th>
                        <td>{this.state.filename}</td>
                    </tr>
                    {comment}
                    </tbody>
                </Table>

            </React.Fragment>
        }
        return <React.Fragment>
            <PageHeader>Configuration</PageHeader>
            {content}
        </React.Fragment>;
    }

    constructor(props) {
        super(props);
        this.state = {
            version: null,
            installerUrl: null,
            baseUrl: null,
            fileSize: null,
            filename: null,
            comment: null
        }
    }
}

const mapStateToProps = state => ({
    updateVersion: state.version.updateVersion
});

export default connect(mapStateToProps, {})(UpdatePage);

