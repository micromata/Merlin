import React from 'react';
import {connect} from 'react-redux';
import {PageHeader} from '../../general/BootstrapComponents';
import {getRestServiceUrl} from '../../../utilities/global';
import {FormButton} from '../../general/forms/FormComponents';
import {Table} from 'reactstrap';
import I18n from "../../general/translation/I18n";


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
        let content = <I18n name={'update.noUpdateAvailable'}/>;
        if (this.props.updateVersion) {
            let comment = null;
            if (this.state.comment) {
                comment = <tr>
                    <th><I18n name={'common.comment'}/></th>
                    <td>{this.state.comment}</td>
                </tr>;
            }
            content = <React.Fragment>
                <h2><I18n name={'update.newVersion'}/></h2>
                <I18n name={'update.newVersionAvailable'} params={[this.props.updateVersion]}/>
                <br/>
                <I18n name={'update.newVersion.simplyClickButton'} />
                <br/>
                <br/>
                <form>
                    <FormButton
                        onClick={this.onUpdate}
                        hintKey={'update.update.button.hint'}
                    >
                        <I18n name={'common.update'}/>
                    </FormButton>
                </form>
                <br/>
                <I18n name={'update.description.line1'}/>
                <br/>
                <I18n name={'update.description.line2'}/>
                <br/>
                <br/>
                <Table striped bordered hover size={'sm'}>
                    <tbody>
                    <tr>
                        <th><I18n name={'version'}/></th>
                        <td>{this.state.version}</td>
                    </tr>
                    <tr>
                        <th><I18n name={'update.installerUrl'}/></th>
                        <td><a href={this.state.installerUrl} target={'_new'}>{this.state.installerUrl}</a></td>
                    </tr>
                    <tr>
                        <th><I18n name={'common.fileSize'}/></th>
                        <td>{this.state.fileSize}</td>
                    </tr>
                    <tr>
                        <th><I18n name={'common.filename'}/></th>
                        <td>{this.state.filename}</td>
                    </tr>
                    {comment}
                    </tbody>
                </Table>

            </React.Fragment>
        }
        return <React.Fragment>
            <PageHeader><I18n name={'update'}/></PageHeader>
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

