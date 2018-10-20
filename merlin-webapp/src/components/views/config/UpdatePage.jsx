import React from 'react';
import {PageHeader} from '../../general/BootstrapComponents';
import {getRestServiceUrl, isDevelopmentMode} from "../../../utilities/global";
import {FormButton, FormField} from "../../general/forms/FormComponents";


class UpdatePage extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            updateVersion: null
        }
        this.onUpdate = this.onUpdate.bind(this);
    }

    componentDidMount() {
        fetch(getRestServiceUrl('version'), {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        })
            .then((resp) => {
                return resp.json()
            })
            .then((data) => {
                if (data.updateVersion) {
                    this.setState({updateVersion: data.updateVersion});
                }
            })
            .catch((error) => {
                console.log(error, "Oups, what's happened?")
            })
    }

    onUpdate(event) {
        fetch(getRestServiceUrl("updates/update"), {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        })
    }

    render() {
        let content = 'No new version available.';
        if (this.state.updateVersion) {
            content = <React.Fragment>
                <h2>New Version</h2>
                <form>
                    <FormButton onClick={this.onUpdate}
                                hint="Download and starts update.">Update
                    </FormButton>
                </form>
            </React.Fragment>
        }
        return <React.Fragment>
            <PageHeader>Configuration</PageHeader>
            {content}
        </React.Fragment>
    }
}

export default UpdatePage;

