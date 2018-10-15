import React from 'react';

class TemplateDefinitionDependentVariables extends React.Component {
    render = () => {
        if (!this.props.definition.dependentVariableDefinitions) {
            return null;
        }
        const rows = [];
        this.props.definition.dependentVariableDefinitions.forEach((variable) => {
            rows.push(
                <div key={variable.name}>{variable.name}</div>
            );
        });
        return <React.Fragment>
            {rows}
        </React.Fragment>;
    };
}

export default TemplateDefinitionDependentVariables;