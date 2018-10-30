import React from 'react';
import {
    FormLabelField,
    FormSelect, FormOption
} from "../../general/forms/FormComponents";
import {getRestServiceUrl} from "../../../utilities/global";
import I18n from "../../general/translation/I18n";
import ErrorAlertGenericRestFailure from "../../general/ErrorAlertGenericRestFailure";
import Loading from "../../general/Loading";

class ConfigAccountTab extends React.Component {
    loadConfig = () => {
        this.setState({
            loading: true,
            failed: false
        });
        fetch(getRestServiceUrl('configuration/user'), {
            method: 'GET',
            dataType: 'JSON',
            headers: {
                'Content-Type': 'text/plain; charset=utf-8'
            }
        })
            .then((resp) => {
                return resp.json()
            })
            .then((data) => {
                this.setState({
                    loading: false,
                    ...data
                })
            })
            .catch((error) => {
                console.log("error", error);
                this.setState({
                    loading: false,
                    failed: true
                });
            })
    };

    constructor(props) {
        super(props);

        this.state = {
            loading: true,
            failed: false,
            language: 'default'
        };

        this.handleTextChange = this.handleTextChange.bind(this);
        this.loadConfig = this.loadConfig.bind(this);
    }

    componentDidMount() {
        this.loadConfig();
    }

    handleTextChange = event => {
        event.preventDefault();
        this.setState({[event.target.name]: event.target.value});
    }


    save(event) {
        var user = {
            language: this.state.language
        };
        fetch(getRestServiceUrl("configuration/user"), {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(user)
        })
    }

    render() {
        if (this.state.loading) {
            return <Loading/>;
        }

        if (this.state.failed) {
            return <ErrorAlertGenericRestFailure handleClick={this.loadConfig} />;
        }

        return (
            <form>
                <FormLabelField label={<I18n name={'configuration.application.language'}/>} fieldLength={2}>
                    <FormSelect value={this.state.language} name={'language'} onChange={this.handleTextChange}>
                        <FormOption value={'default'} i18nKey={'configuration.application.language.option.default'} />
                        <FormOption value={'en'} i18nKey={'language.english'} />
                        <FormOption value={'de'} i18nKey={'language.german'} />
                    </FormSelect>
                </FormLabelField>
            </form>
        );
    }
}

export default ConfigAccountTab;

